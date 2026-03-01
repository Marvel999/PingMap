package com.marvel999.pingmap.feature.speedtest.data

import com.marvel999.pingmap.data.local.dao.SpeedTestDao
import com.marvel999.pingmap.data.local.entity.SpeedTestEntity
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestProgress
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestRepository
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class SpeedTestRepositoryImpl(
    private val okHttpClient: OkHttpClient,
    private val speedTestDao: SpeedTestDao
) : SpeedTestRepository {

override fun runTest(testUrl: String): Flow<SpeedTestProgress> = callbackFlow {
        trySend(SpeedTestProgress("Ping", 0.0))
        val pingMs = measurePing()
        trySend(SpeedTestProgress("Ping", 0.0))

        trySend(SpeedTestProgress("Download", 0.0))
        val downloadMbps = measureDownloadSpeed(testUrl) { trySend(SpeedTestProgress("Download", it)) }

        trySend(SpeedTestProgress("Upload", 0.0))
        val uploadUrl = if (testUrl.contains("__down")) testUrl.replace("__down", "__up") else testUrl
        val uploadMbps = measureUploadSpeed(uploadUrl) { trySend(SpeedTestProgress("Upload", it)) }

        val result = SpeedTestResult(
            downloadMbps = downloadMbps,
            uploadMbps = uploadMbps,
            pingMs = pingMs,
            jitterMs = 0.0,
            packetLoss = 0,
            serverLocation = "",
            isp = "",
            networkType = "WiFi",
            timestamp = System.currentTimeMillis()
        )
        speedTestDao.insert(result.toEntity())
        trySend(SpeedTestProgress("Done", downloadMbps))
        close()
        awaitClose { }
    }.flowOn(Dispatchers.IO)

    override fun getLatestResult(): Flow<SpeedTestResult?> =
        speedTestDao.getLatest().map { it?.toResult() }

    override fun getHistory(limit: Int): Flow<List<SpeedTestResult>> =
        speedTestDao.getRecentResults(limit).map { list -> list.map { it.toResult() } }

    private suspend fun measurePing(): Long = withContext(Dispatchers.IO) {
        val rtts = mutableListOf<Long>()
        repeat(5) {
            val start = System.currentTimeMillis()
            try {
                if (InetAddress.getByName("8.8.8.8").isReachable(2000)) {
                    rtts.add(System.currentTimeMillis() - start)
                }
            } catch (_: Exception) {}
            delay(200)
        }
        rtts.average().toLong().coerceAtLeast(0)
    }

    private suspend fun measureDownloadSpeed(
        url: String,
        onProgress: (Double) -> Unit
    ): Double = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        var totalBytes = 0L
        val startTime = System.currentTimeMillis()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                val body = response.body ?: return@withContext 0.0
                val buffer = ByteArray(8192)
                body.byteStream().use { input ->
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        totalBytes += read
                        val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
                        if (elapsed > 0) {
                            val mbps = (totalBytes * 8) / (elapsed * 1_000_000)
                            onProgress(mbps)
                        }
                    }
                }
            }
        } catch (_: Exception) {
            return@withContext 0.0
        }
        val totalSec = (System.currentTimeMillis() - startTime) / 1000.0
        (totalBytes * 8) / (totalSec * 1_000_000)
    }

    private suspend fun measureUploadSpeed(url: String, onProgress: (Double) -> Unit): Double =
        withContext(Dispatchers.IO) {
            val size = 2_000_000
            val data = ByteArray(size) { 0x41 }
            val startTime = System.currentTimeMillis()
            try {
                val mediaType = "application/octet-stream".toMediaType()
        val request = okhttp3.RequestBody.create(mediaType, data)
                val req = Request.Builder().url(url).post(request).build()
                okHttpClient.newCall(req).execute().use { response ->
                    if (!response.isSuccessful) return@withContext 0.0
                }
            } catch (_: Exception) {
                return@withContext 0.0
            }
            val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
            if (elapsed <= 0) 0.0 else (size * 8) / (elapsed * 1_000_000)
        }
}

private fun SpeedTestResult.toEntity() = SpeedTestEntity(
    id = id,
    downloadMbps = downloadMbps,
    uploadMbps = uploadMbps,
    pingMs = pingMs,
    jitterMs = jitterMs,
    packetLoss = packetLoss,
    serverLocation = serverLocation,
    isp = isp,
    networkType = networkType,
    timestamp = timestamp
)

private fun SpeedTestEntity.toResult() = SpeedTestResult(
    id = id,
    downloadMbps = downloadMbps,
    uploadMbps = uploadMbps,
    pingMs = pingMs,
    jitterMs = jitterMs,
    packetLoss = packetLoss,
    serverLocation = serverLocation,
    isp = isp,
    networkType = networkType,
    timestamp = timestamp
)

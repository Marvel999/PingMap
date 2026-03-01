package com.marvel999.pingmap.feature.ping.data

import com.marvel999.pingmap.feature.ping.domain.PingResult
import com.marvel999.pingmap.feature.ping.domain.PingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.net.InetAddress
import kotlin.math.abs

class PingRepositoryImpl : PingRepository {

    override fun ping(host: String, count: Int): Flow<PingResult> = flow {
        val rtts = mutableListOf<Long>()
        withContext(Dispatchers.IO) {
            repeat(count) {
                val start = System.currentTimeMillis()
                val reachable = try {
                    InetAddress.getByName(host).isReachable(2000)
                } catch (_: Exception) {
                    false
                }
                if (reachable) {
                    rtts.add((System.currentTimeMillis() - start).coerceAtLeast(0))
                }
                if (it < count - 1) delay(200)
            }
        }
        val minMs = rtts.minOrNull() ?: 0L
        val maxMs = rtts.maxOrNull() ?: 0L
        val avgMs = if (rtts.isNotEmpty()) rtts.average().toLong() else 0L
        val jitterMs = if (rtts.size >= 2) {
            rtts.zipWithNext { a, b -> abs((b - a).toDouble()) }.average()
        } else 0.0
        val packetLoss = ((count - rtts.size) * 100.0 / count).toInt().coerceIn(0, 100)
        emit(
            PingResult(
                host = host,
                minMs = minMs,
                avgMs = avgMs,
                maxMs = maxMs,
                jitterMs = jitterMs,
                packetLoss = packetLoss,
                packets = rtts
            )
        )
    }.flowOn(Dispatchers.IO)
}

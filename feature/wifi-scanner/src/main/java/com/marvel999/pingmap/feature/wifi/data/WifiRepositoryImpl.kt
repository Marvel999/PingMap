package com.marvel999.pingmap.feature.wifi.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import com.marvel999.pingmap.feature.wifi.domain.WifiNetwork
import com.marvel999.pingmap.feature.wifi.domain.WifiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class WifiRepositoryImpl(
    private val wifiManager: WifiManager,
    private val context: Context
) : WifiRepository {

    override fun scan(): Flow<List<WifiNetwork>> = flow {
        val success = requestScan()
        val results = getScanResults()
        val list = results.map { it.toWifiNetwork() }.sortedByDescending { it.signalQuality }
        emit(list)
    }.flowOn(Dispatchers.IO)

    private suspend fun requestScan(): Boolean = withContext(Dispatchers.Main.immediate) {
        suspendCancellableCoroutine { cont ->
            var resumed = false
            lateinit var receiver: BroadcastReceiver
            var timeoutJob: Job? = null
            fun safeResume(value: Boolean) {
                if (!resumed) {
                    resumed = true
                    timeoutJob?.cancel()
                    try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
                    cont.resume(value) {}
                }
            }
            receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context, intent: Intent) {
                    val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    safeResume(success)
                }
            }
            @Suppress("DEPRECATION")
            try {
                if (Build.VERSION.SDK_INT >= 33) {
                    context.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), Context.RECEIVER_NOT_EXPORTED)
                } else {
                    context.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
                }
            } catch (e: Exception) {
                safeResume(false)
                return@suspendCancellableCoroutine
            }
            val started = wifiManager.startScan()
            if (!started) {
                safeResume(false)
            } else {
                cont.invokeOnCancellation {
                    timeoutJob?.cancel()
                    try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
                }
                timeoutJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(15_000)
                    safeResume(true)
                }
            }
        }
    }

    private suspend fun getScanResults(): List<ScanResult> = withContext(Dispatchers.IO) {
        wifiManager.scanResults ?: emptyList()
    }
}

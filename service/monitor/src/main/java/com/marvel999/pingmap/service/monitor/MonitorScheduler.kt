package com.marvel999.pingmap.service.monitor

import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Entry point to start or stop background network monitoring.
 * Call start() only after user has granted notification permission (API 33+).
 */
object MonitorScheduler {

    fun start(context: Context) {
        val intent = Intent(context, NetworkMonitorService::class.java).apply { action = NetworkMonitorService.ACTION_START }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            @Suppress("DEPRECATION")
            context.startService(intent)
        }
        NetworkCheckWorker.schedule(context)
    }

    fun stop(context: Context) {
        val intent = Intent(context, NetworkMonitorService::class.java).apply { action = NetworkMonitorService.ACTION_STOP }
        context.stopService(intent)
        NetworkCheckWorker.cancel(context)
    }
}

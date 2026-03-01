package com.marvel999.pingmap.service.monitor

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.marvel999.pingmap.core.network.NetworkMonitor

/**
 * Foreground service for background network monitoring.
 * Shows an ongoing notification and keeps the app eligible for periodic WorkManager checks.
 * User must explicitly start monitoring (e.g. from Home or Settings).
 */
class NetworkMonitorService : Service() {

    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate() {
        super.onCreate()
        networkMonitor = NetworkMonitor(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startForegroundMode()
            ACTION_STOP -> stopSelf()
            else -> startForegroundMode()
        }
        return START_STICKY
    }

    private fun startForegroundMode() {
        val notification = MonitorNotificationHelper.buildOngoingNotification(
            this,
            connected = networkMonitor.isConnected
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(
                this,
                MonitorNotificationHelper.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            )
        } else {
            @Suppress("DEPRECATION")
            startForeground(MonitorNotificationHelper.NOTIFICATION_ID, notification)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.marvel999.pingmap.monitor.START"
        const val ACTION_STOP = "com.marvel999.pingmap.monitor.STOP"
    }
}

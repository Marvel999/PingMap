package com.marvel999.pingmap.service.monitor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object MonitorNotificationHelper {

    const val CHANNEL_ID = "network_monitor"
    const val NOTIFICATION_ID = 9001

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(com.marvel999.pingmap.service.monitor.R.string.notification_channel_monitor_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(com.marvel999.pingmap.service.monitor.R.string.notification_channel_monitor_desc)
            setShowBadge(false)
        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    fun buildOngoingNotification(context: Context, connected: Boolean): android.app.Notification {
        ensureChannel(context)
        val title = if (connected) {
            context.getString(com.marvel999.pingmap.service.monitor.R.string.notification_monitor_connected_title)
        } else {
            context.getString(com.marvel999.pingmap.service.monitor.R.string.notification_monitor_disconnected_title)
        }
        val text = context.getString(com.marvel999.pingmap.service.monitor.R.string.notification_monitor_ongoing_text)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    fun showDisconnectedNotification(context: Context) {
        ensureChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(com.marvel999.pingmap.service.monitor.R.string.notification_monitor_disconnected_title))
            .setContentText(context.getString(com.marvel999.pingmap.service.monitor.R.string.notification_disconnected_message))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID + 1, notification)
    }
}

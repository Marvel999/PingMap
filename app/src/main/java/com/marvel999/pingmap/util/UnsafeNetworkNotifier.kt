package com.marvel999.pingmap.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object UnsafeNetworkNotifier {

    private const val CHANNEL_ID = "wifi_advisor_unsafe"
    private const val NOTIFICATION_ID = 9002

    fun showIfNeeded(context: Context, networkSsid: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "WiFi Advisor",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Warnings about unsafe WiFi networks"
                }
            )
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Unsafe network")
            .setContentText("You're connected to \"$networkSsid\" which scores as unsafe. Consider switching.")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        nm.notify(NOTIFICATION_ID, notification)
    }
}

package com.marvel999.pingmap.service.monitor

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.marvel999.pingmap.core.network.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Periodic WorkManager worker that checks network connectivity.
 * If disconnected, can show a notification (when notifications are allowed).
 * Schedule via MonitorScheduler when user enables background monitoring.
 */
class NetworkCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val monitor = NetworkMonitor(applicationContext)
        return@withContext if (monitor.isConnected) {
            Result.success()
        } else {
            MonitorNotificationHelper.showDisconnectedNotification(applicationContext)
            Result.success()
        }
    }

    companion object {
        private const val WORK_NAME = "network_check"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<NetworkCheckWorker>(30, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}

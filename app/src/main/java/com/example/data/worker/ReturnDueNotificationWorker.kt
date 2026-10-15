package com.example.data.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.EcoLendDatabase
import com.example.data.model.AppNotification
import com.example.data.repository.EcoLendRepository
import java.util.concurrent.TimeUnit

class ReturnDueNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val dao = EcoLendDatabase.getDatabase(context).dao()
            val activeRequests = dao.getActiveRequestsSync()
            val now = System.currentTimeMillis()

            val profile = dao.getUserProfileSync()
            if (profile != null && !profile.loanRemindersEnabled) {
                return Result.success()
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            activeRequests.forEach { request ->
                val diffMillis = request.endDateMillis - now

                when {
                    // Item is past due
                    diffMillis < 0 -> {
                        val hoursPast = ((Math.abs(diffMillis) / (1000 * 3600)).coerceAtLeast(1)).toInt()
                        val title = "Attention: Borrow Past Due! ⚠️"
                        val message = "'${request.itemTitle}' was due back to ${request.ownerName} $hoursPast hour(s) ago. Please coordinate porch return to release your $$${request.depositPaid.toInt()} deposit and restore community trust."

                        // Save in-app notification
                        dao.insertNotification(
                            AppNotification(
                                title = title,
                                message = message,
                                type = "RETURN_REMINDER",
                                timestampMillis = now,
                                isRead = false
                            )
                        )

                        // Post local push notification
                        notificationManager?.let { manager ->
                            val notification = NotificationCompat.Builder(context, EcoLendRepository.CHANNEL_ID)
                                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .build()
                            manager.notify((request.id + 10000L).toInt(), notification)
                        }
                    }

                    // Item is due within the next 24 hours (approaching return date)
                    diffMillis in 0..(24 * 3600 * 1000L) -> {
                        val hoursRemaining = ((diffMillis / (1000 * 3600)).coerceAtLeast(1)).toInt()
                        val title = "Return Due in $hoursRemaining Hours ⏳"
                        val message = "'${request.itemTitle}' is due back to ${request.ownerName} in ~$hoursRemaining hours. Return on time to release your $$${request.depositPaid.toInt()} deposit hold and earn +50 Eco Karma!"

                        // Save in-app notification
                        dao.insertNotification(
                            AppNotification(
                                title = title,
                                message = message,
                                type = "RETURN_REMINDER",
                                timestampMillis = now,
                                isRead = false
                            )
                        )

                        // Post local push notification
                        notificationManager?.let { manager ->
                            val notification = NotificationCompat.Builder(context, EcoLendRepository.CHANNEL_ID)
                                .setSmallIcon(android.R.drawable.ic_dialog_info)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .build()
                            manager.notify((request.id + 5000L).toInt(), notification)
                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "return_due_notification_periodic_worker"

        fun schedulePeriodicWork(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<ReturnDueNotificationWorker>(
                1, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun runOneTimeCheck(context: Context) {
            val oneTimeRequest = OneTimeWorkRequestBuilder<ReturnDueNotificationWorker>().build()
            WorkManager.getInstance(context).enqueue(oneTimeRequest)
        }
    }
}

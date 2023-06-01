package com.rebalance.service.notification

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rebalance.Preferences
import com.rebalance.backend.api.RequestsSender
import com.rebalance.backend.api.jsonArrayToNotification

class NotificationIdle(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(
    context,
    workerParams
) {
    private val notificationService = NotificationService(context)

    override fun doWork(): Result {
        return try {
            val preferences = Preferences(context).read()
            val mainLooper = Looper.getMainLooper()

            val notifications = jsonArrayToNotification(
                RequestsSender.sendGet(
                    "http://${preferences.serverIp}/users/${
                        preferences.userId
                    }/notifications"
                )
            )

            if (notifications.isNotEmpty()) {
                for (notification in notifications) {
                    if (notification.getUserId().toString() == preferences.userId &&
                        notification.getAmount() < 0
                    ) {
                        Handler(mainLooper).post {
                            if (notification.getExpenseId() != -1L) {
                                notificationService.sendNotification("Added new expense")
                            }
                            if (notification.getGroupId() != -1L) {
                                notificationService.sendNotification("Added to new group")
                            }
                        }
                    }
                }
            }
            Result.success()
        } catch (ignored: Throwable) {
            Result.failure()
        }
    }

}

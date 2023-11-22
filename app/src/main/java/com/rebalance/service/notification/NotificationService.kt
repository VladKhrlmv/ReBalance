package com.rebalance.service.notification

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rebalance.R
import com.rebalance.activity.LoadingActivity
import com.rebalance.backend.service.BackendService
import com.rebalance.service.Preferences
import com.rebalance.util.alertUser

class NotificationService(
    val context: Context,
) {
    private var notificationId = 0
    val backendService = BackendService(context)
    fun start() {
        createNotificationChannel()

        val mainLooper = Looper.getMainLooper()
        val preferences = Preferences(context).read()

        Thread {
            try {
                while (true) {
                    val notifications = backendService.getNotifications()

                    if (notifications.isNotEmpty()) {
                        for (notification in notifications) {
                            if (notification.getUserId().toString() == preferences.userId &&
                                notification.getAmount() < 0 &&
                                notification.getUserFromId().toString() != preferences.userId
                            ) {
                                Handler(mainLooper).post {
                                    if (notification.getExpenseId() != -1L) {
                                        sendNotification("Added new expense")
                                    }
                                    if (notification.getGroupId() != -1L) {
                                        sendNotification("Added to new group")
                                    }
                                }
                            }
                        }
                    }

                    Thread.sleep(5_000)
                }
            } catch (_: Exception) {
            }
        }.start()
    }

    private fun createNotificationChannel() {
        val channel1 = NotificationChannel(
            "channel1",
            "Channel 1",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel 1"
            setSound(Uri.parse("android.resource://${context.packageName}/${R.raw.sound1}"), null)
        }

        val channel2 = NotificationChannel(
            "channel2",
            "Channel 2",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel 2"
            setSound(Uri.parse("android.resource://${context.packageName}/${R.raw.sound2}"), null)
        }

        val channel3 = NotificationChannel(
            "channel3",
            "Channel 3",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel 3"
            setSound(Uri.parse("android.resource://${context.packageName}/${R.raw.sound3}"), null)
        }

        val systemChannel = NotificationChannel(
            "systemChannel",
            "System Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "System Channel"
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel1)
        notificationManager.createNotificationChannel(channel2)
        notificationManager.createNotificationChannel(channel3)
        notificationManager.createNotificationChannel(systemChannel)
    }

    fun sendNotification(
        textContent: String
    ) {
        val intent = Intent(context, LoadingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "systemChannel")
            .setSmallIcon(androidx.core.R.drawable.notification_template_icon_bg)
            .setContentTitle("ReBalance")
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.mailicon)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)

        with(NotificationManagerCompat.from(context)) {
            val activity = (context as Activity)
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted, you can show notifications or perform other actions
                notify(notificationId++, builder.build())
            } else {
                alertUser("Cannot show notification - no permission", context)
            }
        }
    }
}

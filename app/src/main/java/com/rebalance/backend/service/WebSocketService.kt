package com.rebalance.backend.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rebalance.R
import com.rebalance.backend.api.RequestParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import java.util.*


class WebSocketService : Service() {
    private val backendService = BackendService.get()
    private lateinit var notificationManager: NotificationManager
    private var channelId = "BackgroundNotificationServiceChannel"
    private lateinit var stompClient: StompClient
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationAndChannel()
    }

    @SuppressLint("CheckResult")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            "${backendService.getStompEndpoint()}/notifications"
        )

        val headers: List<StompHeader> =
            listOf(StompHeader("Authorization", "Bearer ${backendService.getToken()}"))

        stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.CLOSED -> {
                    stopSelf()
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.d("websocket", "Error: ${lifecycleEvent.exception}")
                }
                else -> {}
            }
        }

        stompClient
            .withClientHeartbeat(60000)
            .withServerHeartbeat(60000)
            .connect(headers)

        try {
            if (stompClient.isConnected) {
                stompClient.topic("/user/notifications/new/all").subscribe { topicMessage ->
                    serviceScope.launch {
                        backendService.updateDbFromNotifications(
                            RequestParser.responseToNotificationAll(
                                topicMessage.payload
                            ),
                            true
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("websocket", "Failed to subscribe")
        }

        startForeground(1, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        stompClient.disconnect();
    }

    private fun createNotificationAndChannel() {
        val channelName = "Background notification service channel"
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = channelName
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
        }

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentText("We keep you updated")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent) //TODO: on click launch app
            .setSmallIcon(R.drawable.ic_launcher_round)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setOngoing(true) // do not allow to remove notification
            .setOnlyAlertOnce(true)
            .build()
    }
}

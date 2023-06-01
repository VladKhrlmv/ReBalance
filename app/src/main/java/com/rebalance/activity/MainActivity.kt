package com.rebalance.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.rebalance.service.notification.NotificationIdle
import com.rebalance.service.notification.NotificationService
import com.rebalance.ui.screen.MainScreen
import com.rebalance.ui.theme.ReBalanceTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val workManager = WorkManager.getInstance(application)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                // initialize nav controller
                // the start route is loading screen, so it will be processed first
                val context = LocalContext.current
                val navHostController = rememberNavController()

                val notificationService = NotificationService(context)
                notificationService.start()
                val recurringWork: PeriodicWorkRequest =
                    PeriodicWorkRequest.Builder(NotificationIdle::class.java, 15, TimeUnit.MINUTES)
                        .build()
                workManager.enqueue(recurringWork)

                MainScreen(navHostController)
            }
        }
    }
}

package com.rebalance.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.screen.main.MainScreen
import com.rebalance.ui.theme.ReBalanceTheme
import com.rebalance.util.alertUser

class MainActivity : ComponentActivity() {
    private val workManager = WorkManager.getInstance(application)
    private val backendService = BackendService.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ReBalanceTheme {
                // initialize nav controller
                // the start route is loading screen, so it will be processed first
                val context = LocalContext.current
                val navHostController = rememberNavController()

                // request permission to send notifications, otherwise exit
                val activity = (context as Activity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            101 // request code for POST_NOTIFICATIONS
                        )

                        alertUser(
                            "Application requires permission to send notifications!",
                            context
                        )
                    }
                }

                // start receiving notifications
                backendService.startPollingNotifications()

                MainScreen(navHostController)
            }
        }
    }
}

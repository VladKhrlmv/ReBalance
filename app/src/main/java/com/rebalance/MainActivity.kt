package com.rebalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.rebalance.ui.components.*
import com.rebalance.ui.components.screens.navigation.ScreenNavigation
import com.rebalance.ui.components.screens.navigation.ScreenNavigationItem
import com.rebalance.ui.theme.ReBalanceTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val workManager = WorkManager.getInstance(application)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                val notificationService = NotificationService(LocalContext.current)
                notificationService.start()
                val recurringWork: PeriodicWorkRequest =
                    PeriodicWorkRequest.Builder(NotificationIdle::class.java, 15, TimeUnit.MINUTES)
                        .build()
                workManager.enqueue(recurringWork)

                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var pieChartActive by rememberSaveable { mutableStateOf(true) }
    Scaffold(
        topBar = {
            com.rebalance.ui.components.TopAppBar(pieChartActive, onPieChartActiveChange = {
                pieChartActive = !pieChartActive
            }, true)
        },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = { PlusButton(navController) },
        floatingActionButtonPosition = FabPosition.Center,
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                ScreenNavigation(
                    navController,
                    LocalContext.current,
                    pieChartActive,
                    ScreenNavigationItem.Personal.route
                )
                ToolTipOverlay(context = LocalContext.current, navController = navController)
            }
        }
    )
}

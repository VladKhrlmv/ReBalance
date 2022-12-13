package com.rebalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rebalance.ui.components.BottomNavigationBar
import com.rebalance.ui.components.PlusButton
import com.rebalance.ui.components.screens.navigation.ScreenNavigation
import com.rebalance.ui.theme.ReBalanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var pieChartActive by remember { mutableStateOf(true) }
    Scaffold(
        topBar = { com.rebalance.ui.components.TopAppBar() },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = { PlusButton(navController) },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                ScreenNavigation(navController = navController, pieChartActive)
            }
        }
    )
}

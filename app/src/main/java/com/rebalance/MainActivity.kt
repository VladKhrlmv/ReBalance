package com.rebalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    var pieChartActive by rememberSaveable { mutableStateOf(true) }
    Scaffold(
        topBar = { com.rebalance.ui.components.TopAppBar(pieChartActive, onPieChartActiveChange = {
            pieChartActive = !pieChartActive
        }) },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = { PlusButton(navController) },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                ScreenNavigation(navController, pieChartActive)
            }
        }
    )
}

package com.rebalance.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.rebalance.backend.api.*
import com.rebalance.ui.component.TopAppBar
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.initNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen() {
    var pieChartActive by rememberSaveable { mutableStateOf(true) }
    val navController = rememberNavController()
    Scaffold(
        // TODO: Make pie-chart parameter optional
        topBar = {
            TopAppBar(
                pieChartActive,
                onPieChartActiveChange = {
                    pieChartActive = !pieChartActive
                },
                false,
                navController
            )
        },
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                val navHostController = rememberNavController()
                val navHost = initNavHost(LocalContext.current, navHostController, Routes.Authentication)
            }
        }
    )
}

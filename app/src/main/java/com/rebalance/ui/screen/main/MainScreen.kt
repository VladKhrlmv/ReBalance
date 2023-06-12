package com.rebalance.ui.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebalance.ui.component.ToolTipOverlay
import com.rebalance.ui.component.main.AddSpendingButton
import com.rebalance.ui.component.main.BottomNavigationBar
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.initNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    var pieChartActive by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        topBar = {
            com.rebalance.ui.component.TopAppBar(pieChartActive, onPieChartActiveChange = {
                pieChartActive = !pieChartActive
            }, true, navHostController)
        },
        bottomBar = { BottomNavigationBar(navHostController) },
        floatingActionButton = {
            val navBackStackEntry by navHostController.currentBackStackEntryAsState()
            if (navBackStackEntry?.destination?.route != Routes.AddSpending.route) {
                AddSpendingButton(navHostController)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                // initialize nav graph here so navigation will be inside scaffold
                val navHost = initNavHost(context, navHostController, Routes.Main, pieChartActive)

                ToolTipOverlay(context, navHostController)
            }
        }
    )
}

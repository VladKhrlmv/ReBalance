package com.rebalance.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.rebalance.ui.component.BottomNavigationBar
import com.rebalance.ui.component.PlusButton
import com.rebalance.ui.component.ToolTipOverlay
import com.rebalance.ui.navigation.ScreenNavigation
import com.rebalance.ui.navigation.ScreenNavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var pieChartActive by rememberSaveable { mutableStateOf(true) }
    Scaffold(
        topBar = {
            com.rebalance.ui.component.TopAppBar(pieChartActive, onPieChartActiveChange = {
                pieChartActive = !pieChartActive
            }, true, navController)
        },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = { PlusButton(navController) },
        floatingActionButtonPosition = FabPosition.End,
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

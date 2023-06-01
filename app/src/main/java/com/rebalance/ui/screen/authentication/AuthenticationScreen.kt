package com.rebalance.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rebalance.backend.api.*
import com.rebalance.ui.component.TopAppBar

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
//                initNavHost(
//                    navController,
//                    LocalContext.current,
//                    pieChartActive,
//                    ScreenNavigationItem.SignIn.route
//                )
            }
        }
    )
}

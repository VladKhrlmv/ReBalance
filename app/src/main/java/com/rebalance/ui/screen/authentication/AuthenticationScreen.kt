package com.rebalance.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.initNavHost
import com.rebalance.ui.navigation.navigateSingleTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    navHostController: NavHostController
) {
    var onPlusClick by remember {
        mutableStateOf({
            navigateSingleTo(navHostController, Routes.AddSpending)
        })
    }
    var pieChartActive = mutableStateOf(true)
    Scaffold(
        topBar = {
            com.rebalance.ui.component.main.scaffold.TopAppBar(
                false,
                rememberNavController()
            )
        },
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                // initialize nav graph here so navigation will be inside scaffold
                val navHost =
                    initNavHost(
                        LocalContext.current,
                        navHostController,
                        Routes.Authentication,
                        pieChartActive
                    ) { newOnPlusClick ->
                        onPlusClick = newOnPlusClick
                    }
            }
        }
    )
}

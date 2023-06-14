package com.rebalance.ui.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebalance.ui.component.ToolTipOverlay
import com.rebalance.ui.component.main.scaffold.AddSpendingButton
import com.rebalance.ui.component.main.scaffold.BottomNavigationBar
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.initNavHost
import com.rebalance.ui.navigation.navigateUp
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.PieChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    var pieChartActive by rememberSaveable { mutableStateOf(true) }
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    Scaffold(
        topBar = {
            com.rebalance.ui.component.main.scaffold.TopAppBar(
                true,
                navHostController,
                backButton = {
                    DisplayBackButton(navBackStackEntry, navHostController)
                },
                content = {
                    DisplayPieChartButton(navBackStackEntry, pieChartActive) {
                        pieChartActive = !pieChartActive
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navHostController) },
        floatingActionButton = {
            DisplayAddSpendingButton(navBackStackEntry, navHostController)
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(
                modifier = Modifier.padding(
                    start = padding.calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(layoutDirection = LayoutDirection.Ltr),
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() - 10.dp
                )
            ) {
                // initialize nav graph here so navigation will be inside scaffold
                val navHost = initNavHost(context, navHostController, Routes.Main, pieChartActive)

                // start guided tour
                ToolTipOverlay(context, navHostController)
            }
        }
    )
}

@Composable
private fun DisplayBackButton(
    navBackStackEntry: NavBackStackEntry?,
    navHostController: NavHostController
) {
    // display back button only on Settings or Add Spending screen
    if (navBackStackEntry?.destination?.route == Routes.AddSpending.route ||
        navBackStackEntry?.destination?.route == Routes.Settings.route
    ) {
        IconButton(onClick = {
            navigateUp(navHostController)
        }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Go back"
            )
        }
    }
}

@Composable
private fun DisplayPieChartButton(
    navBackStackEntry: NavBackStackEntry?,
    pieChartActive: Boolean,
    onPieChartClick: () -> Unit
) {
    // display button only if on Personal screen
    if (navBackStackEntry?.destination?.route == Routes.Personal.route) {
        IconButton(
            onClick = { onPieChartClick() }, // invoke callback to switch state of pie chart
            modifier = Modifier.testTag("viewSwitcher")
        ) {
            Icon(
                if (pieChartActive) Icons.Filled.List else EvaIcons.Fill.PieChart,
                "Pie chart or list"
            )
        }
    }
}

@Composable
private fun DisplayAddSpendingButton(
    navBackStackEntry: NavBackStackEntry?,
    navHostController: NavHostController
) {
    // if we are on Add Spending screen, hide this button
    if (navBackStackEntry?.destination?.route != Routes.AddSpending.route) {
        AddSpendingButton(navHostController)
    }
}

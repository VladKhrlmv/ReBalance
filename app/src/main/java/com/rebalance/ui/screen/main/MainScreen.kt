package com.rebalance.ui.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.max
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebalance.ui.component.ToolTipOverlay
import com.rebalance.ui.component.main.scaffold.AddSpendingButton
import com.rebalance.ui.component.main.scaffold.BottomNavigationBar
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.initNavHost
import com.rebalance.ui.navigation.navigateSingleTo
import com.rebalance.ui.navigation.navigateUp
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.PieChart
import compose.icons.evaicons.fill.Plus
import compose.icons.evaicons.fill.Save

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navHostController: NavHostController
) {
    var onPlusClick by remember {
        mutableStateOf({
            navigateSingleTo(navHostController, Routes.AddSpending)
        })
    }
    val context = LocalContext.current
    val pieChartActive = rememberSaveable { mutableStateOf(true) } //TODO: move to settings
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val imePadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()

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
                        pieChartActive.value = !pieChartActive.value
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navHostController) },
        floatingActionButton = { },
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(
                modifier = Modifier.padding(
                    start = padding.calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(layoutDirection = LayoutDirection.Ltr),
                    top = padding.calculateTopPadding(),
                    bottom = max(
                        padding.calculateBottomPadding(),
                        imePadding
                    )
                )
            ) {
                // initialize nav graph here so navigation will be inside scaffold
                initNavHost(context, navHostController, Routes.Main, pieChartActive) {
                    newOnPlusClick -> onPlusClick = newOnPlusClick
                }

                // start guided tour
                ToolTipOverlay(navHostController)

                // plus button
                DisplayAddSpendingButton(
                    navBackStackEntry,
                    onPlusClick,
                    Modifier.align(Alignment.BottomEnd)
                )
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
        navBackStackEntry?.destination?.route == Routes.Settings.route ||
        navBackStackEntry?.destination?.route == Routes.GroupSettings.paramRoute
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
    pieChartActive: MutableState<Boolean>,
    onPieChartClick: () -> Unit
) {
    // display button only if on Personal screen
    if (navBackStackEntry?.destination?.route == Routes.Personal.route) {
        IconButton(
            onClick = { onPieChartClick() }, // invoke callback to switch state of pie chart
            modifier = Modifier.testTag("viewSwitcher")
        ) {
            Icon(
                if (pieChartActive.value) Icons.Filled.List else EvaIcons.Fill.PieChart,
                "Pie chart or list"
            )
        }
    }
}

@Composable
private fun DisplayAddSpendingButton(
    navBackStackEntry: NavBackStackEntry?,
    onClick: () -> Unit,
    modifier: Modifier
) {
    if (
        navBackStackEntry?.destination?.route != Routes.GroupSettings.paramRoute &&
        navBackStackEntry?.destination?.route != Routes.Settings.route
    ) {
        AddSpendingButton(
            onClick,
            if (navBackStackEntry?.destination?.route == Routes.AddSpending.route)
                EvaIcons.Fill.Save
            else
                EvaIcons.Fill.Plus,
            modifier
        )
    }
}

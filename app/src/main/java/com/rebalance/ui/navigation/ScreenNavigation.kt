package com.rebalance.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.rebalance.ui.screen.authentication.SignInScreen
import com.rebalance.ui.screen.authentication.SignUpMailScreen
import com.rebalance.ui.screen.authentication.SignUpScreen
import com.rebalance.ui.screen.main.AddSpendingScreen
import com.rebalance.ui.screen.main.GroupScreen
import com.rebalance.ui.screen.main.PersonalScreen
import com.rebalance.ui.screen.main.SettingsScreen

@Composable
fun initNavHost(
    context: Context,
    navHostController: NavHostController,
    startRoute: Routes,
    pieChartActive: Boolean = true
) {
    return NavHost(
        navHostController,
        startDestination = startRoute.route
    ) {
        navigation(
            startDestination = Routes.Login.route,
            route = Routes.Authentication.route
        ) {
            composable(Routes.Login.route) {
                SignInScreen(context, navHostController)
            }
            composable(Routes.Register.route) {
                SignUpScreen(navHostController)
            }
            composable(Routes.RegisterMail.route) {
                SignUpMailScreen(context, navHostController)
            }
        }
        navigation(
            startDestination = Routes.Personal.route,
            route = Routes.Main.route
        ) {
            composable(Routes.Personal.route) {
                PersonalScreen(context, pieChartActive)
            }
            composable(Routes.Group.route) {
                GroupScreen(context)
            }
            composable(Routes.AddSpending.route) {
                AddSpendingScreen(context, navHostController)
            }
            composable(Routes.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

fun navigateTo(navHostController: NavHostController, route: Routes) {
    navHostController.navigate(route.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navHostController.graph.findStartDestination().id) {
            saveState = true
        }
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

fun navigateTabs(navHostController: NavHostController, route: Routes, forgetRoute: Routes) {
    // remove add spending and settings screens from stack
    navHostController.popBackStack(Routes.AddSpending.route, inclusive = true)
    navHostController.popBackStack(Routes.Settings.route, inclusive = true)
    navHostController.navigate(route.route) {
        // remove previous routes from stack
        popUpTo(forgetRoute.route) {
            saveState = true
            inclusive = true
        }
        // Restore state when reselecting a previously selected tab
        restoreState = true
    }
}

fun navigateSingleTo(navHostController: NavHostController, route: Routes) {
    navHostController.navigate(route.route) {
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

fun navigateWithoutBack(
    navHostController: NavHostController,
    route: Routes,
    forgetRoute: Routes
) {
    navHostController.navigate(route.route) {
        popUpTo(forgetRoute.route) {
            inclusive = true
        }
    }
}

fun navigateUp(navHostController: NavHostController) {
    navHostController.navigateUp()
}

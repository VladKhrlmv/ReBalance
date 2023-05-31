package com.rebalance.ui.components.screens.navigation

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rebalance.*
import com.rebalance.ui.components.screens.AddSpendingScreen
import com.rebalance.ui.components.screens.GroupScreen
import com.rebalance.ui.components.screens.PersonalScreen
import com.rebalance.ui.components.screens.SettingsScreen

@Composable
fun ScreenNavigation(
    navController: NavHostController,
    context: Context,
    pieChart: Boolean,
    startDestination: String
) {
    NavHost(navController, startDestination = startDestination) {
        composable(ScreenNavigationItem.Personal.route) {
            PersonalScreen(context, pieChart)
            BackHandler(true) {
                // do nothing
            }
        }
        composable(ScreenNavigationItem.Group.route) {
            GroupScreen(context)
            BackHandler(true) {
                // do nothing
            }
        }
        composable(ScreenNavigationItem.AddSpending.route) {
            AddSpendingScreen(context)
            BackHandler(true) {
                // do nothing
            }
        }
        composable(ScreenNavigationItem.SignIn.route) {
            SignInScreen(context, navController)
            BackHandler(true) {
                // do nothing
            }
        }
        composable(ScreenNavigationItem.SignUp.route) {
            SignUpScreen(navController)
            BackHandler(true) {
                // do nothing
            }
        }
        composable(ScreenNavigationItem.SignUpMail.route) {
            SignUpMailScreen(context, navController)
            BackHandler(true) {
                // do nothing
            }
        }
        composable("mainActivity") {
            MainActivity()
            BackHandler(true) {
                // do nothing
            }
        }
        composable("signInActivity") {
            SignInActivity()
            BackHandler(true) {
                // do nothing
            }
        }
        composable(ScreenNavigationItem.Settings.route) {
            SettingsScreen(navController)
            BackHandler(true) {
                // do nothing
            }
        }
    }
}
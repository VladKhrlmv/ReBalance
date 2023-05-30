package com.rebalance.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rebalance.activity.MainActivity
import com.rebalance.activity.SignInActivity
import com.rebalance.ui.screen.*

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
        }
        composable(ScreenNavigationItem.Group.route) {
            GroupScreen(context)
        }
        composable(ScreenNavigationItem.AddSpending.route) {
            AddSpendingScreen(context)
        }
        composable(ScreenNavigationItem.SignIn.route) {
            SignInScreen(context, navController)
        }
        composable(ScreenNavigationItem.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(ScreenNavigationItem.SignUpMail.route) {
            SignUpMailScreen(context, navController)
        }
        composable("mainActivity") {
            MainActivity()
        }
        composable("signInActivity") {
            SignInActivity()
        }
        composable(ScreenNavigationItem.Settings.route) {
            SettingsScreen(navController)
        }
    }
}

package com.rebalance.ui.components.screens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rebalance.SignInScreen
import com.rebalance.SignUpMailScreen
import com.rebalance.SignUpScreen
import com.rebalance.ui.components.screens.AddSpendingScreen
import com.rebalance.ui.components.screens.GroupScreen
import com.rebalance.ui.components.screens.PersonalScreen

@Composable
fun ScreenNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = ScreenNavigationItem.SignIn.route) {
        composable(ScreenNavigationItem.Personal.route) {
            PersonalScreen()
        }
        composable(ScreenNavigationItem.Group.route) {
            GroupScreen()
        }
        composable(ScreenNavigationItem.AddSpending.route) {
            AddSpendingScreen()
        }
        composable(ScreenNavigationItem.SignIn.route) {
            SignInScreen(navController)
        }
        composable(ScreenNavigationItem.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(ScreenNavigationItem.SignUpMail.route) {
            SignUpMailScreen(navController)
        }
    }
}
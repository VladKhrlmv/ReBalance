package com.rebalance.ui.components.screens.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rebalance.ui.components.screens.AddSpendingScreen
import com.rebalance.ui.components.screens.GroupScreen
import com.rebalance.ui.components.screens.PersonalScreen

@Composable
fun ScreenNavigation(
    navController: NavHostController,
    context: Context,
    pieChart: Boolean
) {
    NavHost(navController, startDestination = ScreenNavigationItem.Personal.route) {
        composable(ScreenNavigationItem.Personal.route) {
            PersonalScreen(context, pieChart)
        }
        composable(ScreenNavigationItem.Group.route) {
            GroupScreen(context)
        }
        composable(ScreenNavigationItem.AddSpending.route) {
            AddSpendingScreen(context)
        }
    }
}
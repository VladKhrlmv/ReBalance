package com.rebalance.ui.component.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateSingleTo

@Composable
fun BottomNavigationBar(navHostController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        NavigationBarItem(
            icon = { Icons.Filled.Person },
            label = { Text(text = "Personal") },
            alwaysShowLabel = true,
            selected = currentRoute == Routes.Personal.route,
            onClick = {
                navigateSingleTo(navHostController, Routes.Personal)
            }
        )
        NavigationBarItem(
            icon = { Icons.Outlined.Person },
            label = { Text(text = "Group") },
            alwaysShowLabel = true,
            selected = currentRoute == Routes.Group.route,
            onClick = {
                navigateSingleTo(navHostController, Routes.Group)
            }
        )
    }
}

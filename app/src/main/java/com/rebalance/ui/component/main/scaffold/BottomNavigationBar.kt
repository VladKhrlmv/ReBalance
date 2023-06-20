package com.rebalance.ui.component.main.scaffold

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateTabs
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.People
import compose.icons.evaicons.fill.Person

@Composable
fun BottomNavigationBar(navHostController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        NavigationBarItem(
            icon = { Icon(EvaIcons.Fill.Person, contentDescription = "Personal") },
            label = { Text(text = "Personal") },
            selected = currentRoute == Routes.Personal.route,
            onClick = {
                navigateTabs(navHostController, Routes.Personal, Routes.Group)
            }
        )
        NavigationBarItem(
            icon = { Icon(EvaIcons.Fill.People, contentDescription = "Group") },
            label = { Text(text = "Group") },
            selected = currentRoute == Routes.Group.route,
            onClick = {
                navigateTabs(navHostController, Routes.Group, Routes.Personal)
            }
        )
    }
}

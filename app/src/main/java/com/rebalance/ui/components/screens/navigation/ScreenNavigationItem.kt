package com.rebalance.ui.components.screens.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenNavigationItem(var route: String, var icon: ImageVector, var title: String) {
    object Personal : ScreenNavigationItem("personal", Icons.Filled.Person, "Personal")
    object Group : ScreenNavigationItem("group", Icons.Outlined.Person, "Group")
}

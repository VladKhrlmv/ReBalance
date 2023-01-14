package com.rebalance.ui.components.screens.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenNavigationItem(var route: String, var icon: ImageVector, var title: String) {
    object Personal : ScreenNavigationItem("personal", Icons.Filled.Person, "Personal")
    object Group : ScreenNavigationItem("group", Icons.Outlined.Person, "Group")
    object AddSpending : ScreenNavigationItem("add_spending", Icons.Filled.Add, "AddSpending")
    object SignIn : ScreenNavigationItem("sign_in", Icons.Filled.Add, "SignIn")
    object SignUp : ScreenNavigationItem("sign_up", Icons.Filled.Add, "SignUp")
    object SignUpMail : ScreenNavigationItem("sign_mail", Icons.Filled.Add, "SignUpMail")
}

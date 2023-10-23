package com.rebalance.ui.navigation

enum class Routes(
    val route: String,
    val paramRoute: String = "",
) {
    Authentication("auth"),
    Login("login"),
    Register("register"),
    RegisterMail("register-mail"),

    Main("main"),
    Personal("personal"),
    Group("group"),
    GroupSettings("group-settings", "group-settings/{groupId}"),
    AddSpending("add-spending"),
    Settings("settings");
}

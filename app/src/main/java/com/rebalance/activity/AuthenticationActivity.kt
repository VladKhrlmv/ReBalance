package com.rebalance.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.rebalance.ui.theme.ReBalanceTheme
import com.rebalance.ui.screen.authentication.AuthenticationScreen


class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                val navHostController = rememberNavController()

                AuthenticationScreen(navHostController)
            }
        }
    }
}

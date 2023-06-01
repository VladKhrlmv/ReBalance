package com.rebalance.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rebalance.ui.screen.authentication.AuthenticationScreen
import com.rebalance.ui.theme.ReBalanceTheme


class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                AuthenticationScreen()
            }
        }
    }
}

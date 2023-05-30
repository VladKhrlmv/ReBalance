package com.rebalance.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rebalance.ui.screen.LoadingScreen
import com.rebalance.ui.theme.ReBalanceTheme

class LoadingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                LoadingScreen()
            }
        }
    }
}

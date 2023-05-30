package com.rebalance.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rebalance.ui.screen.MainSignInScreen
import com.rebalance.ui.theme.ReBalanceTheme


class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
//                val preferences = Preferences(LocalContext.current).read()
//                if (!preferences.exists()) {
                MainSignInScreen()
//                } else {
//                    val context = LocalContext.current
//                    context.startActivity(Intent(context, MainActivity::class.java))
//                }
            }
        }
    }
}

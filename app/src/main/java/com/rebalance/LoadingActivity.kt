package com.rebalance

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.rebalance.backend.api.sendGet
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



@Composable
fun LoadingScreen() {
    val context = LocalContext.current
    val preferences = Preferences(LocalContext.current).read()
    val isLoading = remember { mutableStateOf(true) }
    Scaffold(
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )  {
                Text(text = "ReBalance", fontSize = 30.sp, modifier = Modifier.padding(90.dp))
                if (isLoading.value) {
                    CircularProgressIndicator()
                }
            }
        }
    )
    try {
        sendGet("http://${preferences.serverIp}/connect")
        if (!preferences.exists()) {
            context.startActivity(Intent(context, SignInActivity::class.java))
        } else {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
        isLoading.value = false
    } catch (exception: Exception) {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(
                context,
                "Can't establish connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}
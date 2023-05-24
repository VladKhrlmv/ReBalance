package com.rebalance

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.ui.theme.ReBalanceTheme
import com.rebalance.utils.alertUser
import java.net.HttpURLConnection
import java.net.URL

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

    val connected = remember { mutableStateOf(0) }

    Scaffold(
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ReBalance", fontSize = 30.sp, modifier = Modifier.padding(90.dp))
                if (isLoading.value) {
                    CircularProgressIndicator()
                }
            }
        }
    )

    tryConnect(preferences) {
        connected.value = it
    }

    if (connected.value == 1) {
        if (!preferences.exists()) {
            context.startActivity(Intent(context, SignInActivity::class.java))
        } else {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    } else if (connected.value == -1) {
        alertUser("Can't establish connection", context)
    }
}

fun tryConnect(
    preferences: PreferencesData,
    onConnect: (Int) -> Unit
) {
    val mainLooper = Looper.getMainLooper()

    Thread {
        try {
            val imageUrl = URL("http://${preferences.serverIp}/connect/test")

            val httpConnection = imageUrl.openConnection() as HttpURLConnection
            httpConnection.doInput = true
            httpConnection.connect()

            Handler(mainLooper).post {
                onConnect(1)
            }
        } catch (e: Exception) {
            onConnect(-1)
        }
    }.start()
}

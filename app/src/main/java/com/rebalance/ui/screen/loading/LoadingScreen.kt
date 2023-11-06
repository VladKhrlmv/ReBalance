package com.rebalance.ui.screen.loading

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.activity.AuthenticationActivity
import com.rebalance.activity.MainActivity
import com.rebalance.ui.navigation.switchActivityTo
import com.rebalance.util.alertUser
import kotlinx.coroutines.delay
import java.net.HttpURLConnection
import java.net.URL


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

    LaunchedEffect(Unit) {
        while (true) {
            tryConnect(preferences) {
                connected.value = it
            }


            delay(5000)
        }
    }

    if (connected.value == 1) {
        if (!preferences.exists()) {
            switchActivityTo(context, AuthenticationActivity::class)
        } else {
            switchActivityTo(context, MainActivity::class)
        }
    } else if (connected.value == -1) {
        alertUser("Can't establish connection. Retrying in 5 seconds", context)
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

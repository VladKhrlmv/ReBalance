package com.rebalance.ui.screen.loading

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
import com.rebalance.backend.service.BackendService
import com.rebalance.util.alertUser
import kotlinx.coroutines.delay


@Composable
fun LoadingScreen() {
    val context = LocalContext.current
    val backendService = BackendService(context)
    val isLoading = remember { mutableStateOf(true) }
    val connected = remember { mutableStateOf(false) }

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
            connected.value = backendService.checkConnectivity()

            delay(5000)
        }
    }

    if (connected.value) {
//        if (!preferences.exists()) {
//            switchActivityTo(context, AuthenticationActivity::class)
//        } else {
//            switchActivityTo(context, MainActivity::class)
//        }
        alertUser("Success", context)
    } else {
        alertUser("Can't establish connection. Retrying in 5 seconds", context)
    }
}

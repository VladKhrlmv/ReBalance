package com.rebalance.ui.screen.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.activity.AuthenticationActivity
import com.rebalance.activity.MainActivity
import com.rebalance.backend.dto.LoginResult
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.navigation.switchActivityTo
import com.rebalance.util.alertUser


@Composable
fun LoadingScreen() {
    val context = LocalContext.current
    val backendService = remember { BackendService.get() }
    var connected by remember { mutableStateOf(LoginResult.Placeholder) }
    var backendServiceInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // initialize BackendService (will be done only once per application launch)
        backendService.initialize(context) {
            backendServiceInitialized = true
        }
    }
    if (backendServiceInitialized) {
        LaunchedEffect(Unit) {
            connected = backendService.checkLogin()
        }
    }

    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ReBalance", fontSize = 30.sp, modifier = Modifier.padding(90.dp))
                when (connected) {
                    LoginResult.LoggedIn -> switchActivityTo(context, MainActivity::class)
                    LoginResult.TokenInspired -> switchActivityTo(
                        context,
                        AuthenticationActivity::class
                    )
                    LoginResult.ServerUnreachable -> alertUser( //TODO: go to main screen with offline mode
                        "Server unavailable. Please try again later",
                        context
                    )
                    else -> CircularProgressIndicator()
                }
            }
        }
    )
}

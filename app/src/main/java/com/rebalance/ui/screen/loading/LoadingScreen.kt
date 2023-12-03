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
import com.rebalance.activity.AuthenticationActivity
import com.rebalance.activity.MainActivity
import com.rebalance.backend.dto.LoginResult
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.navigation.switchActivityTo
import kotlinx.coroutines.delay


@Composable
fun LoadingScreen() {
    val context = LocalContext.current
    val backendService = remember { BackendService(context) }
    val connected = remember { mutableStateOf(LoginResult.ServerUnreachable) }

    LaunchedEffect(Unit) {
        while (connected.value == LoginResult.ServerUnreachable) {
            connected.value = backendService.checkLogin()
            delay(5000)
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
                when (connected.value) {
                    LoginResult.LoggedIn -> switchActivityTo(context, MainActivity::class)
                    LoginResult.TokenInspired -> switchActivityTo(
                        context,
                        AuthenticationActivity::class
                    )
                    LoginResult.ServerUnreachable -> CircularProgressIndicator()
                }
            }
        }
    )
}

package com.rebalance.ui.screen.authentication

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.activity.MainActivity
import com.rebalance.backend.api.dto.request.ApiLoginRequest
import com.rebalance.backend.dto.LoginResult
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.authentication.CustomInput
import com.rebalance.ui.component.authentication.CustomPasswordInput
import com.rebalance.ui.component.authentication.PrimaryButton
import com.rebalance.ui.component.authentication.SecondaryButton
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateTo
import com.rebalance.ui.navigation.switchActivityTo
import com.rebalance.util.alertUser
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(context: Context, navHostController: NavHostController) {
    val backendService = remember { BackendService.get() }
    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val loginFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val loginScope = rememberCoroutineScope()
    var loginResult by remember { mutableStateOf(LoginResult.Placeholder) }
    var isLoginLoading by remember { mutableStateOf(false) }

    Scaffold(
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 35.sp
                    )

                    CustomInput(
                        "Login",
                        login,
                        focusRequester = loginFocusRequester,
                        nextFocusRequester = passwordFocusRequester
                    )
                    CustomPasswordInput(
                        "Password",
                        password,
                        focusRequester = passwordFocusRequester
                    )

                    PrimaryButton("SIGN IN", 20.dp, isLoginLoading, onClick = {
                        // validate data
                        if (login.value.isEmpty() || password.value.isEmpty()) {
                            alertUser("No empty fields allowed", context)
                            return@PrimaryButton
                        }

                        // initiate logging
                        loginScope.launch {
                            isLoginLoading = true
                            loginResult = backendService.login(
                                ApiLoginRequest(
                                    login.value,
                                    password.value
                                )
                            )
                            Log.d("login", "result ${loginResult.name}")
                            isLoginLoading = false
                        }
                    })
                    SecondaryButton("SIGN UP", 5.dp, onClick = {
                        navigateTo(navHostController, Routes.Register)
                    })

                    when (loginResult) {
                        LoginResult.Placeholder -> {}
                        LoginResult.LoggedIn -> switchActivityTo(
                            context,
                            MainActivity::class
                        )
                        LoginResult.BadCredentials -> alertUser("Bad credentials", context)
                        else -> alertUser("Please try again later", context)
                    }
                }
            }
        }
    )
}

package com.rebalance.ui.screen.authentication

import android.content.Context
import android.os.StrictMode
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.activity.MainActivity
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.authentication.CustomInput
import com.rebalance.ui.component.authentication.CustomPasswordInput
import com.rebalance.ui.component.authentication.PrimaryButton
import com.rebalance.ui.component.authentication.SecondaryButton
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateTo
import com.rebalance.ui.navigation.switchActivityTo
import com.rebalance.util.alertUser

@Composable
fun SignInScreen(context: Context, navHostController: NavHostController) {
    val backendService = BackendService(context)
    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val loginFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

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

                    PrimaryButton("SIGN IN", 20.dp, onClick = {
                        if (login.value.isEmpty() || password.value.isEmpty()) {
                            alertUser("No empty fields allowed", context)
                            return@PrimaryButton
                        }
                        try {
                            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                            StrictMode.setThreadPolicy(policy)
                            val user = backendService.login(
                                login.value,
                                password.value
                            )

                            val groups = backendService.getGroups(user.getId())
                            for (group in groups) {
                                if (group.getName() == "per${user.getEmail()}") {
//                                    val preferencesData =
//                                        PreferencesData(
//                                            "",
//                                            user.getId().toString(),
//                                            group.getId(),
//                                            false,
//                                            "systemChannel"
//                                        )
//                                    Preferences(context).write(preferencesData)
                                }
                            }

                            switchActivityTo(context, MainActivity::class)
                        } catch (error: Exception) {
                            alertUser("Wrong email or password", context)
                        }
                    })
                    SecondaryButton("SIGN UP", 5.dp, onClick = {
                        navigateTo(navHostController, Routes.Register)
                    })
                }
            }
        }
    )
}

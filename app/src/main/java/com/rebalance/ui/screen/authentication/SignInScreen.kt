package com.rebalance.ui.screen.authentication

import android.content.Context
import android.os.StrictMode
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.activity.MainActivity
import com.rebalance.backend.api.RequestsSender
import com.rebalance.backend.api.jsonArrayToExpenseGroups
import com.rebalance.backend.api.login
import com.rebalance.ui.component.authentication.CustomInput
import com.rebalance.ui.component.authentication.CustomPasswordInput
import com.rebalance.ui.component.authentication.PrimaryButton
import com.rebalance.ui.component.authentication.SecondaryButton
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateTo
import com.rebalance.ui.navigation.switchActivityTo
import com.rebalance.utils.alertUser

@Composable
fun SignInScreen(context: Context, navHostController: NavHostController) {
    val preferences = rememberSaveable { Preferences(context).read() }

    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

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

                    CustomInput("Login", login, focusRequester = loginFocusRequester, nextFocusRequester = passwordFocusRequester)
                    CustomPasswordInput("Password", password, focusRequester = passwordFocusRequester)

                    PrimaryButton("SIGN IN", 20.dp, onClick = {
                        if (login.value.isEmpty() || password.value.isEmpty()) {
                            alertUser("No empty fields allowed", context)
                            return@PrimaryButton
                        }
                        try {
                            // TODO: Uncomment
                            println("trying to login...")
                            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                            StrictMode.setThreadPolicy(policy)
                            val user = login(
                                "http://${preferences.serverIp}/users/login",
                                login.value.trim(),
                                password.value.trim()
                            )
                            println(user)

                            val groupsJson =
                                RequestsSender.sendGet("http://${preferences.serverIp}/users/${user.getId()}/groups")
                            val groups = jsonArrayToExpenseGroups(groupsJson)
                            for (group in groups) {
                                if (group.getName() == "per${user.getEmail()}") {
                                    val preferencesData =
                                        PreferencesData(
                                            "",
                                            user.getId().toString(),
                                            group.getId(),
                                            false,
                                            "systemChannel"
                                        )
                                    Preferences(context).write(preferencesData)
                                    println("Logged in as: ${preferences.userId}")
                                    println("Personal group: ${preferences.groupId}")
                                }
                            }
//                             throw FailedLoginException("Invalid password for email")

                            switchActivityTo(context, MainActivity::class)
                        } catch (error: Exception) {
                            println("Caught a FailedLoginException! You should see the error message on the screen")
                            showError.value = true
                            errorMessage.value = error.message.toString()
                        }

                    })
                    SecondaryButton("SIGN UP", 5.dp, onClick = {
                        navigateTo(navHostController, Routes.Register)
                    })

                    if (showError.value) {
                        alertUser("Wrong email or password", context)
                    }
                    showError.value = false
                }

            }
        }
    )
}

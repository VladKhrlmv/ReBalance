package com.rebalance.ui.screen.authentication

import android.content.Context
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.activity.MainActivity
import com.rebalance.backend.api.entities.ExpenseGroup
import com.rebalance.backend.exceptions.ServerException
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.authentication.*
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateTo
import com.rebalance.ui.navigation.switchActivityTo
import com.rebalance.util.alertUser

@Composable
fun SignUpMailScreen(context: Context, navHostController: NavHostController) {
    val preferences = rememberSaveable { Preferences(context).read() }

    val email = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatPassword = remember { mutableStateOf("") }
    val personalCurrency = remember { mutableStateOf("") }

    val emailFocusRequester = remember { FocusRequester() }
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val repeatPasswordFocusRequester = remember { FocusRequester() }
    val personalCurrencyFocusRequester = remember { FocusRequester() }
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
                        text = "Sign Up",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 35.sp
                    )

                    CustomInput(
                        "E-mail",
                        email,
                        focusRequester = emailFocusRequester,
                        nextFocusRequester = usernameFocusRequester
                    )
                    CustomInput(
                        "Username",
                        username,
                        focusRequester = usernameFocusRequester,
                        nextFocusRequester = passwordFocusRequester
                    )
                    CustomPasswordInput(
                        "Password",
                        password,
                        focusRequester = passwordFocusRequester,
                        nextFocusRequester = repeatPasswordFocusRequester,
                        ImeAction.Next
                    )
                    CustomPasswordInput(
                        "Repeat password",
                        repeatPassword,
                        focusRequester = repeatPasswordFocusRequester,
                        nextFocusRequester = personalCurrencyFocusRequester,
                        ImeAction.Next
                    )
                    CurrencyInput(personalCurrency, focusRequester = personalCurrencyFocusRequester)

                    PrimaryButton("SIGN UP", 20.dp, onClick = {
                        if (
                            email.value.isEmpty()
                            || password.value.isEmpty()
                            || username.value.isEmpty()
                            || repeatPassword.value.isEmpty()
                            || personalCurrency.value.isEmpty()
                        ) {
                            alertUser("No empty fields allowed", context)
                            return@PrimaryButton
                        } else if (personalCurrency.value.length != 3) {
                            alertUser("Currency must have exactly 3 symbols", context)
                            return@PrimaryButton
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value)
                                .matches()
                        ) {
                            alertUser("Invalid email format", context)
                            return@PrimaryButton
                        }
                        try {
                            if (password.value != repeatPassword.value) {
                                alertUser("Passwords mismatch!", context)
                                return@PrimaryButton
                            }

                            BackendService(preferences).register(
                                email.value,
                                username.value,
                                password.value
                            )

                            val userByNickname =
                                BackendService(preferences).getUserByEmail(email.value)
                            val group: ExpenseGroup = BackendService(preferences).createGroup(
                                personalCurrency.value,
                                "per${email.value}",
                                userByNickname.getId()
                            )

                            val preferencesData = PreferencesData(
                                "",
                                userByNickname.getId().toString(),
                                group.getId(),
                                true,
                                "systemChannel"
                            )

                            Preferences(context).write(preferencesData)

                            switchActivityTo(context, MainActivity::class)
                        } catch (error: ServerException) {
                            alertUser(error.message.toString(), context)
                        }
                    })
                    SecondaryButton("SIGN IN", 5.dp, onClick = {
                        navigateTo(navHostController, Routes.Login)
                    })
                }
            }
        }
    )
}

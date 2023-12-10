package com.rebalance.ui.screen.authentication

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.activity.MainActivity
import com.rebalance.backend.api.dto.request.ApiRegisterRequest
import com.rebalance.backend.dto.RegisterResult
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.authentication.*
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateTo
import com.rebalance.ui.navigation.switchActivityTo
import com.rebalance.util.alertUser
import kotlinx.coroutines.launch

@Composable
fun SignUpMailScreen(context: Context, navHostController: NavHostController) {
    val backendService = remember { BackendService.get() }

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

    val registerScope = rememberCoroutineScope()
    var registerResult by remember { mutableStateOf(RegisterResult.Placeholder) }
    var isRegisterLoading by remember { mutableStateOf(false) }

    Scaffold(
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
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

                    PrimaryButton("SIGN UP", 20.dp, isRegisterLoading, onClick = {
                        // validate fields
                        if (
                            email.value.isEmpty()
                            || password.value.isEmpty()
                            || username.value.isEmpty()
                            || repeatPassword.value.isEmpty()
                            || personalCurrency.value.isEmpty()
                        ) {
                            alertUser("No empty fields allowed", context)
                            return@PrimaryButton
                        } else if (personalCurrency.value.length != 3) { //TODO: add more password validations
                            alertUser("Currency must have exactly 3 symbols", context)
                            return@PrimaryButton
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value)
                                .matches()
                        ) {
                            alertUser("Invalid email format", context)
                            return@PrimaryButton
                        }
                        if (password.value != repeatPassword.value) {
                            alertUser("Passwords mismatch!", context)
                            return@PrimaryButton
                        }

                        registerScope.launch {
                            isRegisterLoading = true
                            registerResult = backendService.register(
                                ApiRegisterRequest(
                                    email.value,
                                    password.value,
                                    username.value,
                                    personalCurrency.value
                                )
                            )
                            isRegisterLoading = false
                        }
                    })
                    SecondaryButton("SIGN IN", 5.dp, onClick = {
                        navigateTo(navHostController, Routes.Login)
                    })

                    when (registerResult) {
                        RegisterResult.Placeholder -> {}
                        RegisterResult.Registered -> switchActivityTo(
                            context,
                            MainActivity::class
                        )
                        RegisterResult.EmailAlreadyTaken -> alertUser(
                            "Email already taken",
                            context
                        )
                        RegisterResult.IncorrectData -> alertUser("Incorrect data", context)
                        else -> alertUser("Please try again later", context)
                    }
                }
            }
        }
    )
}

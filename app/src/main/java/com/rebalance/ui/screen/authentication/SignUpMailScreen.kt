package com.rebalance.ui.screen.authentication

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.activity.MainActivity
import com.rebalance.backend.api.RequestsSender
import com.rebalance.backend.api.jsonToApplicationUser
import com.rebalance.backend.api.jsonToExpenseGroup
import com.rebalance.backend.api.register
import com.rebalance.backend.entities.ExpenseGroup
import com.rebalance.backend.exceptions.PasswordMismatchException
import com.rebalance.backend.exceptions.ServerException
import com.rebalance.ui.component.authentication.*
import com.rebalance.ui.navigation.ScreenNavigationItem
import com.rebalance.utils.alertUser

@Composable
fun SignUpMailScreen(context: Context, navController: NavController) {
    val preferences = rememberSaveable { Preferences(context).read() }

    val email = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatPassword = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    val pass = remember { mutableStateOf("") }
    val personalCurrency = remember { mutableStateOf("") }
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

                    CustomInput("E-mail", email)
                    CustomInput("Username", username)
                    CustomPasswordInput("Password", password)
                    CustomPasswordInput("Repeat password", repeatPassword)
                    CurrencyInput(personalCurrency)
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
                        }
                        try {
                            if (password.value != repeatPassword.value) {
                                throw PasswordMismatchException("Passwords do not match")
                            }
//                            throw ServerException("Something went wrong, please try later")
                            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                            StrictMode.setThreadPolicy(policy)
                            println("trying to register...")
                            val loginAndPassword = register(
                                "http://${preferences.serverIp}/users",
                                email.value,
                                username.value,
                                password.value
                            )
                            pass.value = loginAndPassword.getPassword()
                            val userByNickname =
                                jsonToApplicationUser(RequestsSender.sendGet("http://${preferences.serverIp}/users/email/${email.value}"))
                            println(userByNickname)
                            val groupCreationResult = RequestsSender.sendPost(
                                "http://${preferences.serverIp}/users/${userByNickname.getId()}/groups",
                                Gson().toJson(
                                    ExpenseGroup(
                                        "per${email.value}",
                                        personalCurrency.value
                                    )
                                )
                            )
                            println(groupCreationResult)

                            val preferencesData = PreferencesData(
                                "",
                                userByNickname.getId().toString(),
                                jsonToExpenseGroup(groupCreationResult).getId(),
                                true,
                                "systemChannel"
                            )

                            Preferences(context).write(preferencesData)


                            context.startActivity(Intent(context, MainActivity::class.java))
                        } catch (error: ServerException) {
                            println("Caught a ServerException!")
                            showError.value = true
                            errorMessage.value = error.message.toString()
                            alertUser(error.message.toString(), context)
                        }

                    })
                    SecondaryButton("SIGN IN", 5.dp, onClick = {
                        navController.navigate(ScreenNavigationItem.SignIn.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            // Avoid multiple copies of the same destination when
                            // re-selecting the same item
                            launchSingleTop = true
                            // Restore state when re-selecting a previously selected item
                            restoreState = true
                        }
                    })
//                    if (showError.value) {
//                        alertUser(errorMessage.value, context)
//                    } else {
//                        alertUser(pass.value, context)
//                    }
//                    showError.value = false
                }

            }
        }
    )
}

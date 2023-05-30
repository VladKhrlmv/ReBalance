package com.rebalance.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.rebalance.backend.api.*
import com.rebalance.backend.entities.ExpenseGroup
import com.rebalance.backend.exceptions.PasswordMismatchException
import com.rebalance.backend.exceptions.ServerException
import com.rebalance.ui.navigation.ScreenNavigation
import com.rebalance.ui.navigation.ScreenNavigationItem
import com.rebalance.ui.theme.ReBalanceTheme
import com.rebalance.ui.theme.md_theme_light_onPrimary
import com.rebalance.utils.alertUser

val currencyRegex = """[a-zA-Z]{0,3}""".toRegex()

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
//                val preferences = Preferences(LocalContext.current).read()
//                if (!preferences.exists()) {
                MainSignInScreen()
//                } else {
//                    val context = LocalContext.current
//                    context.startActivity(Intent(context, MainActivity::class.java))
//                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSignInScreen() {
    var pieChartActive by rememberSaveable { mutableStateOf(true) }
    val navController = rememberNavController()
    Scaffold(
        // TODO: Make pie-chart parameter optional
        topBar = {
            com.rebalance.ui.component.TopAppBar(
                pieChartActive,
                onPieChartActiveChange = {
                    pieChartActive = !pieChartActive
                },
                false,
                navController
            )
        },
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                ScreenNavigation(
                    navController,
                    LocalContext.current,
                    pieChartActive,
                    ScreenNavigationItem.SignIn.route
                )
            }
        }
    )
}

@Composable
fun SignInScreen(context: Context, navController: NavController) {
    val preferences = rememberSaveable { Preferences(context).read() }

    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
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

                    CustomInput("Login", login)
                    CustomPasswordInput("Password", password)
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
                                login.value,
                                password.value
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

                            context.startActivity(Intent(context, MainActivity::class.java))
                        } catch (error: Exception) {
                            println("Caught a FailedLoginException! You should see the error message on the screen")
                            showError.value = true
                            errorMessage.value = error.message.toString()
                        }

                    })
                    SecondaryButton("SIGN UP", 5.dp, onClick = {
                        navController.navigate(ScreenNavigationItem.SignUp.route) {
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

                    if (showError.value) {
                        alertUser("Wrong email or password", context)
                    }
                    showError.value = false
                }

            }
        }
    )
}

@Composable
fun SignUpScreen(navController: NavController) {
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
                            .padding(20.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 35.sp
                    )

//                    ReferenceButton("Google", 20.dp, R.drawable.google50, onClick = {})
//                    ReferenceButton("Facebook", 20.dp, R.drawable.facebook48, onClick = {})
                    ReferenceButton("Mail", 20.dp, R.drawable.mailicon, onClick = {
                        navController.navigate(ScreenNavigationItem.SignUpMail.route) {
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
                    SecondaryButton("SIGN IN", 20.dp, onClick = {
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
                }
            }
        }
    )
}

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

@Composable
fun CustomInput(label: String, textState: MutableState<String>) {
    TextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        label = { Text(text = label) },
        modifier = Modifier.padding(8.dp),
        singleLine = true
    )
}

@Composable
fun CustomPasswordInput(label: String, textState: MutableState<String>) {
    val passwordVisible = remember { mutableStateOf(false) }
    TextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        label = { Text(text = label) },
        modifier = Modifier.padding(8.dp),
        visualTransformation = if (passwordVisible.value) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible.value)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val description = if (passwordVisible.value) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(imageVector = image, description)
            }
        },
        singleLine = true
    )
}

@Composable
fun PrimaryButton(label: String, paddingTop: Dp, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = paddingTop)
            .width(180.dp)
            .height(50.dp),
        shape = RoundedCornerShape(40.dp)
    ) {
        Text(
            text = label,
            fontSize = 18.sp
        )
    }
}

@Composable
fun SecondaryButton(label: String, paddingTop: Dp, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .padding(top = paddingTop)
    ) {
        Text(
            text = label,
            fontSize = 18.sp
        )
    }
}

@Composable
fun ReferenceButton(label: String, paddingTop: Dp, image: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = paddingTop)
            .width(250.dp)
            .height(45.dp),
        shape = RoundedCornerShape(40.dp),
//        border = BorderStroke(1.dp, Color.Gray),
//        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
    ) {
        Box {
            Image(
                painterResource(id = image),
                contentDescription = "icon",
                modifier = Modifier
                    .size(25.dp),
                colorFilter = ColorFilter.tint(color = md_theme_light_onPrimary)

            )
            Text(
                text = label,
//                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp
            )
        }

    }
}

@Composable
fun CurrencyInput(personalCurrency: MutableState<String>) {
    TextField(
        value = personalCurrency.value,
        onValueChange = { newCurrency ->
            if (currencyRegex.matches(newCurrency)) {
                personalCurrency.value = newCurrency.uppercase()
            }
        },
        label = {
            Text(text = "Your currency")
        },
        modifier = Modifier.padding(8.dp),
        singleLine = true
    )
}

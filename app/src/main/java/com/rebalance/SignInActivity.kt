package com.rebalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rebalance.backend.GlobalVars
import com.rebalance.backend.api.login
import com.rebalance.backend.exceptions.FailedLoginException
import com.rebalance.backend.exceptions.ServerException
import com.rebalance.ui.components.BottomNavigationBar
import com.rebalance.ui.components.PlusButton
import com.rebalance.ui.components.TopAppBar
import com.rebalance.ui.components.screens.navigation.ScreenNavigation
import com.rebalance.ui.components.screens.navigation.ScreenNavigationItem
import com.rebalance.ui.theme.ReBalanceTheme

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                MainSignInScreen()
            }
        }
    }
}

@Composable
fun MainSignInScreen() {
    var pieChartActive by rememberSaveable { mutableStateOf(true) }
    val navController = rememberNavController()
    Scaffold(
        // TODO: Make piechart parameter optional
        topBar = { com.rebalance.ui.components.TopAppBar(pieChartActive, onPieChartActiveChange = {
            pieChartActive = !pieChartActive
        }) },
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                ScreenNavigation(navController, pieChartActive)
            }
        }
    )
}

@Composable
fun SignInScreen(navController: NavController) {
    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var showError = false
    var errorMessage = ""
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
                        try {
                            // TODO: Save the user
                            System.out.println("trying to login...")
                            var user = login("http://${GlobalVars().getIp()}/user/login/${login.value}", login.value, password.value)
                            System.out.println("logged in")

                            showError = false

                            navController.navigate(ScreenNavigationItem.Personal.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        } catch (error: FailedLoginException) {
                            println("Caught a FailedLoginException! You should see the error message on the screen")
                            showError = true
                            errorMessage = error.message.toString()
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
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    })
                    if (showError) Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        Text(text = errorMessage, color = Color.Red)
                    }
                }

            }
        }
    )
}

@Composable
fun SignUpScreen(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
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

                    ReferenceButton("Google", 20.dp, R.drawable.google50, onClick = {})
                    ReferenceButton("Facebook", 20.dp, R.drawable.facebook48, onClick = {})
                    ReferenceButton("Mail", 20.dp, R.drawable.mail, onClick = {
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
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
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
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    })
                }
            }
        }
    )
}

@Composable
fun SignUpMailScreen(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val email = remember { mutableStateOf("") }
    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatPassword = remember { mutableStateOf("") }
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
                    CustomInput("Login", login)
                    CustomPasswordInput("Password", password)
                    CustomPasswordInput("Repeat password", repeatPassword)
                    PrimaryButton("SIGN UP", 20.dp, onClick = {
                        try {
                            System.out.println("trying to register...")
                            var user = login("http://${GlobalVars().getIp()}/user/login/${login.value}", login.value, password.value)
                            System.out.println("registered!")


                            navController.navigate(ScreenNavigationItem.Personal.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        } catch (error: ServerException) {
                            println("Caught a ServerException!")
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
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    })
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
        modifier = Modifier.padding(8.dp)
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

            IconButton(onClick = {passwordVisible.value = !passwordVisible.value}){
                Icon(imageVector  = image, description)
            }
        }
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
        .padding(top = paddingTop)) {
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
        border = BorderStroke(1.dp, Color.Gray),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
    ) {
        Box {
            Image(
                painterResource(id = image),
                contentDescription ="icon",
                modifier = Modifier
                    .size(25.dp)
            )
            Text(
                text = label,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp
            )
        }

    }
}
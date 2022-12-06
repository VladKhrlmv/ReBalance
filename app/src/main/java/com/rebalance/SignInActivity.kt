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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.rebalance.ui.components.TopAppBar
import com.rebalance.ui.theme.ReBalanceTheme

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                SignUpScreen()
            }
        }
    }
}

@Composable
fun SignInScreen() {
//    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar() },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
//                ScreenNavigation(navController = navController)
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

                    CustomInput("Login")
                    CustomPasswordInput("Password")
                    PrimaryButton("SIGN IN", 20.dp)
                    SecondaryButton("SIGN UP", 5.dp)
                }

            }
        }
    )
}

@Composable
fun SignUpScreen() {
//    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar() },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
//                ScreenNavigation(navController = navController)
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

                    ReferenceButton("Google", 20.dp, R.drawable.google50)
                    ReferenceButton("Facebook", 20.dp, R.drawable.facebook48)
                    ReferenceButton("Mail", 20.dp, R.drawable.mail)
                    SecondaryButton("SIGN IN", 20.dp)
                }
            }
        }
    )
}

@Composable
fun SignUpEmailScreen() {
//    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar() },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
//                ScreenNavigation(navController = navController)
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

                    CustomInput("E-mail")
                    CustomInput("Login")
                    CustomPasswordInput("Password")
                    CustomPasswordInput("Repeat password")
                    PrimaryButton("SIGN UP", 20.dp)
                    SecondaryButton("SIGN IN", 5.dp)
                }

            }
        }
    )
}

@Composable
fun CustomInput(label: String) {
    val textState = remember { mutableStateOf(TextFieldValue()) }
    TextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        label = { Text(text = label) },
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun CustomPasswordInput(label: String) {
    val textState = remember { mutableStateOf(TextFieldValue()) }
    val passwordVisible = remember { mutableStateOf(true) }
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
fun PrimaryButton(label: String, paddingTop: Dp) {
    Button(onClick = {
        //your onclick code here
    },
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
fun SecondaryButton(label: String, paddingTop: Dp) {
    TextButton(onClick = { /* Do something! */ },
        modifier = Modifier
        .padding(top = paddingTop)) {
        Text(
            text = label,
            fontSize = 18.sp
        )
    }
}

@Composable
fun ReferenceButton(label: String, paddingTop: Dp, image: Int) {
    Button(onClick = {
        //your onclick code here
    },
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

@Preview
@Composable
fun PreviewSignIn() {
    ReBalanceTheme {
        SignUpEmailScreen()
    }
}
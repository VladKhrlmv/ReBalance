package com.rebalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.rebalance.ui.components.TopAppBar
import com.rebalance.ui.theme.ReBalanceTheme

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                SignInScreen()
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
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        text = "Sign In",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp
                    )

                    CustomInput("Login")
                    CustomInput("Password")
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
        modifier = Modifier.padding(Dp(4F))
    )
}

@Preview
@Composable
fun PreviewSignIn() {
    ReBalanceTheme {
        SignInScreen()
    }
}
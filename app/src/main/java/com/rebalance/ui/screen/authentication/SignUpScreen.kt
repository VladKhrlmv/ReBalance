package com.rebalance.ui.screen.authentication

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.R
import com.rebalance.ui.component.authentication.ReferenceButton
import com.rebalance.ui.component.authentication.SecondaryButton
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateTo
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Email

@Composable
fun SignUpScreen(navHostController: NavHostController) {
    Scaffold(
        content = { padding ->
            val focusManager: FocusManager = LocalFocusManager.current
            Box(modifier = Modifier
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { /* Do nothing on press to avoid ripple effect */
                        },
                        onTap = { focusManager.clearFocus() }
                    )
                }
            ) {
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
                    ReferenceButton("Mail", 20.dp, EvaIcons.Fill.Email, onClick = {
                        navigateTo(navHostController, Routes.RegisterMail)
                    })
                    SecondaryButton("SIGN IN", 20.dp, onClick = {
                        navigateTo(navHostController, Routes.Login)
                    })
                }
            }
        }
    )
}

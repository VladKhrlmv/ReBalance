package com.rebalance.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rebalance.R
import com.rebalance.ui.component.authentication.ReferenceButton
import com.rebalance.ui.component.authentication.SecondaryButton
import com.rebalance.ui.navigation.ScreenNavigationItem

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

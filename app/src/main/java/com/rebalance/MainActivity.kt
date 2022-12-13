package com.rebalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rebalance.ui.components.BottomNavigationBar
import com.rebalance.ui.components.CardsScreen
import com.rebalance.ui.components.CardsViewModel
import com.rebalance.ui.components.PlusButton
import com.rebalance.ui.components.screens.navigation.ScreenNavigation
import com.rebalance.ui.theme.ReBalanceTheme

class MainActivity : ComponentActivity() {
    private val cardsViewModel by viewModels<CardsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReBalanceTheme {
                CardsScreen(cardsViewModel)
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { com.rebalance.ui.components.TopAppBar() },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = { PlusButton(navController) },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                ScreenNavigation(navController = navController)
            }
        }
    )
}

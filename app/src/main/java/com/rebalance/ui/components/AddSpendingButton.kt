package com.rebalance.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rebalance.R
import com.rebalance.ui.components.screens.navigation.ScreenNavigationItem
import com.rebalance.ui.theme.AddSpendingButtonShape

@Composable
fun PlusButton(navController: NavController) {
    FloatingActionButton(
        shape = AddSpendingButtonShape,
        modifier = Modifier.size(65.dp),
        onClick = {
            navController.navigate(ScreenNavigationItem.AddSpending.route) {
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
        }
    ) {
        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.plus_button_description))
    }
}
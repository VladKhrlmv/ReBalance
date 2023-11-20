package com.rebalance.ui.component.main.scaffold

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.rebalance.R
import com.rebalance.ui.theme.AddSpendingButtonShape

@Composable
fun AddSpendingButton(
    navBackStackEntry: NavBackStackEntry?,
    navHostController: NavHostController,
    onClick: () -> Unit,
    icon: ImageVector
) {
    FloatingActionButton(
        shape = AddSpendingButtonShape,
        modifier = Modifier.size(65.dp),
        onClick = onClick,
    ) {
        Icon(
            icon,
            contentDescription = stringResource(R.string.plus_button_description)
        )
    }
}

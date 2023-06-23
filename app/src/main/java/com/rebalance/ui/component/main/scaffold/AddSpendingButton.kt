package com.rebalance.ui.component.main.scaffold

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rebalance.R
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateSingleTo
import com.rebalance.ui.theme.AddSpendingButtonShape

@Composable
fun AddSpendingButton(
    navHostController: NavHostController
) {
    FloatingActionButton(
        shape = AddSpendingButtonShape,
        modifier = Modifier.size(65.dp),
        onClick = {
            navigateSingleTo(navHostController, Routes.AddSpending)
        },
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = stringResource(R.string.plus_button_description)
        )
    }
}

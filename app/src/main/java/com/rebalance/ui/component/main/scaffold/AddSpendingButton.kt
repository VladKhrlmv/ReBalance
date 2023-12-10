package com.rebalance.ui.component.main.scaffold

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rebalance.R
import com.rebalance.ui.theme.AddSpendingButtonShape

@Composable
fun AddSpendingButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier
) {
    FloatingActionButton(
        shape = AddSpendingButtonShape,
        modifier = modifier
            .size(80.dp)
            .padding(end = 15.dp, bottom = 15.dp),
        onClick = onClick,
    ) {
        Icon(
            icon,
            contentDescription = stringResource(R.string.plus_button_description)
        )
    }
}

package com.rebalance.ui.component.authentication

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun SecondaryButton(label: String, paddingTop: Dp, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .padding(top = paddingTop)
    ) {
        Text(
            text = label,
            fontSize = 18.sp
        )
    }
}

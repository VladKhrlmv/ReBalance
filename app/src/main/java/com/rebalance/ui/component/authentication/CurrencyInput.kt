package com.rebalance.ui.component.authentication

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val currencyRegex = """[a-zA-Z]{0,3}""".toRegex() //TODO: move to utils

@Composable
fun CurrencyInput(personalCurrency: MutableState<String>) {
    TextField(
        value = personalCurrency.value,
        onValueChange = { newCurrency ->
            if (currencyRegex.matches(newCurrency)) {
                personalCurrency.value = newCurrency.uppercase()
            }
        },
        label = {
            Text(text = "Your currency")
        },
        modifier = Modifier.padding(8.dp),
        singleLine = true
    )
}

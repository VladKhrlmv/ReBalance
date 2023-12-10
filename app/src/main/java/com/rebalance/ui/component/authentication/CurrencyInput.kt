package com.rebalance.ui.component.authentication

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rebalance.util.currencyRegex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyInput(
    personalCurrency: MutableState<String>,
    focusRequester: FocusRequester = FocusRequester(),
    nextFocusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Done
) {
    TextField(
        value = personalCurrency.value,
        onValueChange = { newCurrency ->
            if (currencyRegex().matches(newCurrency)) {
                personalCurrency.value = newCurrency.uppercase()
            }
        },
        label = {
            Text(text = "Your currency")
        },
        placeholder = { Text("ABC") },
        modifier = Modifier
            .padding(8.dp)
            .focusRequester(focusRequester),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions(
            onNext = { nextFocusRequester?.requestFocus() }
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent
        )
    )
}

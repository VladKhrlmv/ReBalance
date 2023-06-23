package com.rebalance.ui.component.authentication

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CustomInput(
    label: String,
    textState: MutableState<String>,
    focusRequester: FocusRequester = FocusRequester(),
    nextFocusRequester: FocusRequester? = null
) {
    TextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        label = { Text(text = label) },
        modifier = Modifier
            .padding(8.dp)
            .focusRequester(focusRequester),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
            onNext = { nextFocusRequester?.requestFocus() }
        )
    )
}

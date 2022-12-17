package com.rebalance.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable
fun DatePickerField() {
    val textState = remember { mutableStateOf(TextFieldValue()) }
    var dateDialog = MaterialDialog(
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker { date ->
            textState.value = TextFieldValue(date.toString())
        }
    }
    Column(modifier = Modifier.padding(16.dp)) {

    }
}
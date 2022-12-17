package com.rebalance.ui.components.screens

import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val costValueRegex = """^\d{0,12}[\.\,]{0,1}\d{0,2}${'$'}""".toRegex()

@Composable
fun AddSpendingScreen() {
    var spendingName by remember { mutableStateOf(TextFieldValue()) }
    var expandedDropdownCategory by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var costValue by remember { mutableStateOf(TextFieldValue()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
            .padding(10.dp)
    ) {
        Text(
            text = "Add spending",
            modifier = Modifier
                .align(Alignment.Start)
                .padding(10.dp),
            fontFamily = FontFamily(Typeface.DEFAULT),
            fontSize = 32.sp
        )
        Row {
            TextField(
                value = spendingName,
                onValueChange = { newSpendingName -> spendingName = newSpendingName },
                placeholder = { Text(text = "Title") },
                modifier = Modifier
                    .padding(10.dp)
            )
            Box (contentAlignment = Alignment.Center) {
                IconButton(onClick = { expandedDropdownCategory = true } ) {
                    Row {
                        Icon(
                            imageVector = if (expandedDropdownCategory) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
                            contentDescription = "Open categories",
                            modifier = Modifier
                                .size(64.dp)
                        )
                    }
                }
                DropdownMenu(expanded = expandedDropdownCategory, onDismissRequest = { expandedDropdownCategory = false }) {
                    DropdownMenuItem(onClick = { selectedCategory = "Sport" }) {
                        // icon can be placed before text
                        Text(text = "Sport")
                    }
                    DropdownMenuItem(onClick = { selectedCategory = "Clothing" }) {
                        Text(text = "Clothing")
                    }
                }
            }
        }
        Row {
            TextField(
                value = costValue,
                onValueChange = { newCostValue ->
                    if (costValueRegex.matches(newCostValue.text)) {
                        costValue = newCostValue
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text(text = "0.00") },
                modifier = Modifier
                    .padding(10.dp)
                    .onFocusChanged {
                        if (!it.isFocused) {
                            var tempCostValue = costValue.text
                                .replace(",", ".")
                                .replace("""^\.""".toRegex(), "0.")
                                .replace("""\.$""".toRegex(), ".00");
                            costValue = TextFieldValue(tempCostValue)
                        }
                    }
            )

        }
        Row {

        }
    }
}
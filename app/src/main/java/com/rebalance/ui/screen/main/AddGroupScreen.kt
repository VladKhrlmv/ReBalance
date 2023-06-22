package com.rebalance.ui.screen.main

import android.content.Context
import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.Preferences
import com.rebalance.utils.alertUser
import com.rebalance.utils.createGroup
import com.rebalance.utils.currencyRegex

@Composable
fun AddGroupScreen(
    context: Context,
    onCancel: () -> Unit,
    onCreate: (Long) -> Unit
) {
    val preferences = rememberSaveable { Preferences(context).read() }

    var groupName by remember { mutableStateOf(TextFieldValue()) }
    var groupCurrency by remember { mutableStateOf(TextFieldValue()) }
    Column(
        modifier = Modifier
            .padding(10.dp)
    ) {
        Text(
            text = "Add group",
            modifier = Modifier
                .padding(10.dp),
            fontFamily = FontFamily(Typeface.DEFAULT),
            fontSize = 32.sp,
        )
        TextField(
            value = groupName,
            onValueChange = { newGroupName -> groupName = newGroupName },
            placeholder = { Text(text = "Name") },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )
        TextField(
            value = groupCurrency,
            onValueChange = { newGroupCurrency ->
                if (currencyRegex().matches(newGroupCurrency.text)) {
                    groupCurrency = newGroupCurrency
                }
            },
            label = {
                Text(text = "Currency")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        Row(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Button(
                onClick = {
                    onCancel()
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val group =
                        createGroup(groupCurrency, groupName, context, preferences) ?: return@Button
                    alertUser("Group was created!", context)
                    onCancel()
                    onCreate(group.getId())
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text("Save")
            }
        }
    }
}

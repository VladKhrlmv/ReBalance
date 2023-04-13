package com.rebalance.ui.components.screens

import android.content.Context
import android.graphics.Typeface
import android.os.StrictMode
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.Preferences
import com.rebalance.backend.api.RequestsSender
import com.rebalance.backend.api.jsonToExpenseGroup
import com.rebalance.utils.alertUser

val currencyRegex = """[A-Z]{0,3}""".toRegex()

@Composable
fun AddGroupScreen(
    context: Context,
    dialogController: MutableState<Boolean>,
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
                if (currencyRegex.matches(newGroupCurrency.text)) {
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
                    dialogController.value = !dialogController.value
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    if (groupCurrency.text.length != 3 || groupName.text.isBlank()) {
                        alertUser("Fill in all fields!", context)
                        return@Button
                    }
                    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                    StrictMode.setThreadPolicy(policy)
                    val group = jsonToExpenseGroup(RequestsSender.sendPost(
                        "http://${preferences.serverIp}/users/${preferences.userId}/groups",
                        "{\"currency\": \"${groupCurrency.text}\", \"name\": \"${groupName.text}\"}"
                    ))
                    alertUser("Group was created!", context)
                    dialogController.value = !dialogController.value
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

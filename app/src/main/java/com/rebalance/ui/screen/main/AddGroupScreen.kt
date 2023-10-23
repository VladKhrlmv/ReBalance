package com.rebalance.ui.screen.main

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.entities.ExpenseGroup
import com.rebalance.backend.service.BackendService
import com.rebalance.utils.alertUser
import com.rebalance.utils.currencyRegex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupScreen(
    context: Context,
    onCancel: () -> Unit,
    onCreate: (Long) -> Unit
) {
    val preferences = rememberSaveable { Preferences(context).read() }

    var groupName by remember { mutableStateOf(TextFieldValue()) }
    var groupCurrency by remember { mutableStateOf(TextFieldValue()) }
    Surface(
        modifier = Modifier
            .padding(10.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Create group",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { /* Focus the next text field, etc. */ }),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = groupCurrency,
                onValueChange = { if (currencyRegex().matches(it.text))
                    groupCurrency = it },
                label = { Text("Currency") },
                placeholder = { Text("ABC") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* Handle the 'done' action */ }),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCancel,
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        // Handle group creation
                        val group = createGroup(groupCurrency, groupName, context, preferences) ?: return@Button
                        alertUser("Group was created!", context)
                        onCancel()
                        onCreate(group.getId())
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}

fun createGroup(
    groupCurrency: TextFieldValue,
    groupName: TextFieldValue,
    context: Context,
    preferences: PreferencesData
): ExpenseGroup? {
    if (groupCurrency.text.length != 3 || groupName.text.isBlank()) {
        alertUser("Fill in all fields!", context)
        return null
    }
    val group = BackendService(preferences).createGroup(groupCurrency.text, groupName.text)
    alertUser("Group was created!", context)
    return group
}

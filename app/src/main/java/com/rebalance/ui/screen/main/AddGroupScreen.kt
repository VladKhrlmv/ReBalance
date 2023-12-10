package com.rebalance.ui.screen.main

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.backend.api.dto.request.ApiGroupCreateRequest
import com.rebalance.backend.service.BackendService
import com.rebalance.util.alertUser
import com.rebalance.util.currencyRegex
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupScreen(
    context: Context,
    backendService: BackendService,
    onClose: () -> Unit,
    onCreate: (Long) -> Unit
) {
    val focusManager: FocusManager = LocalFocusManager.current
    var groupName by remember { mutableStateOf("") }
    var groupCurrency by remember { mutableStateOf("") }

    val addGroupScope = rememberCoroutineScope()
    var newGroupId by remember { mutableLongStateOf(-1L) }

    Surface(
        modifier = Modifier
            .padding(10.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { /* Do nothing on press to avoid ripple effect */
                    },
                    onTap = { focusManager.clearFocus() }
                )
            },
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
                onValueChange = {
                    if (currencyRegex().matches(it))
                        groupCurrency = it.uppercase()
                },
                label = { Text("Currency") },
                placeholder = { Text("ABC") }, //TODO: choose from list of existing codes
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
                    onClick = onClose,
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (groupCurrency.length != 3 || groupName.isBlank()) {
                            alertUser("Fill in all fields!", context)
                            return@Button
                        }

                        addGroupScope.launch {
                            newGroupId = backendService.createGroup(
                                ApiGroupCreateRequest(
                                    groupName, groupCurrency
                                )
                            )
                        }
                    }
                ) {
                    Text("Save")
                }

                if (newGroupId != -1L) {
                    onCreate(newGroupId)
                }
            }
        }
    }
}

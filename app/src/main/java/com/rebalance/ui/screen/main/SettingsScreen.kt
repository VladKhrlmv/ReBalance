package com.rebalance.ui.screen.main

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.service.Preferences
import com.rebalance.R
import com.rebalance.ui.component.SoundPlayer
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Collapse
import compose.icons.evaicons.fill.Expand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var theme = remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager: FocusManager = LocalFocusManager.current

    Box (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { /* Do nothing on press to avoid ripple effect */
                    },
                    onTap = { focusManager.clearFocus() }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
        ) {
            ExpandableSection(title = "Personal", expanded = true) {
                TextField(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true
                )

                TextField(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true
                )

                Button(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    onClick = { showDialog = true }
                ) {
                    Text("Change Password")
                }

                if (showDialog) {
                    PasswordChangeDialog(onDismiss = { showDialog = false })
                }
            }

            ExpandableSection(title = "Preferences", expanded = false) {

                ThemeDropdown(theme = theme)

                val selectedChannel =
                    remember { mutableStateOf(Preferences(context).read().currNotificationChannel) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally)
                ) {
                    SoundPlayer(
                        0,
                        "Default System Sound",
                        selectedChannel,
                        context,
                        "systemChannel",
                        isSystemSound = true
                    )
                    SoundPlayer(R.raw.sound1, "Sound 1", selectedChannel, context, "channel1")
                    SoundPlayer(R.raw.sound2, "Sound 2", selectedChannel, context, "channel2")
                    SoundPlayer(R.raw.sound3, "Sound 3", selectedChannel, context, "channel3")
                }
            }
        }

        Button(
            onClick = {  },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Save", fontSize = 16.sp)
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    expanded: Boolean,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(expanded) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Icon(
                imageVector = if (expanded) EvaIcons.Fill.Expand else EvaIcons.Fill.Collapse,
                contentDescription = "Expand/Collapse"
            )
        }
        if (expanded) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordChangeDialog(onDismiss: () -> Unit) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column {
                TextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Old Password") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                TextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {

                onDismiss()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDropdown(
    theme: MutableState<String>
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = theme.value,
            onValueChange = { },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            label = {
                Text(text = "Theme")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            DropdownMenuItem(
                text = { Text("Light") },
                onClick = {
                    theme.value = "Light"
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Dark") },
                onClick = {
                    theme.value = "Dark"
                    expanded = false
                }
            )
        }
    }
}

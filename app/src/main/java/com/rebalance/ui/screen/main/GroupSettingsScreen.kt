package com.rebalance.ui.screen.main

import android.content.Context
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.exceptions.ServerException
import com.rebalance.backend.service.BackendService
import com.rebalance.util.alertUser
import com.rebalance.util.currencyRegex
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Trash
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSettingsScreen(
    context: Context,
    navHostController: NavHostController,
    groupId: Long,
) {
    val preferences: PreferencesData = Preferences(context).read()
    var group = BackendService(preferences).getGroupById(groupId)
    var groupName by remember { mutableStateOf(group.getName()) }
    var groupCurrency by remember { mutableStateOf(group.getCurrency()) }
    var groupMembers by remember { mutableStateOf(group.getUsers().toList()) }
    val scrollState = rememberScrollState()
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager: FocusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { /* Do nothing on press to avoid ripple effect */
                    },
                    onTap = { focusManager.clearFocus() }
                )
            },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = groupName,
                    onValueChange = { newGroupName -> groupName = newGroupName },
                    label = { Text("Group Name", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .width(270.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    )
                )
                TextField(
                    value = groupCurrency,
                    onValueChange = { newCurrency ->
                        if (currencyRegex().matches(newCurrency))
                            groupCurrency = newCurrency
                    },
                    singleLine = true,
                    label = { Text("Currency", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    )
                )
            }
            DisplayInviteFields(
                preferences = preferences,
                groupId = groupId,
                onUserAdd = {
                    group = BackendService(preferences).getGroupById(groupId)
                    groupMembers = group.getUsers().toList()
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            ) {
                Text(text = "Members",
                    Modifier
                        .padding(horizontal = 10.dp),
                    fontSize = 30.sp
                )
            }
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
            ) {
                for (it in groupMembers) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(text = it.getUsername())
                        IconButton(onClick = { /* Handle member deletion */ }) {
                            Icon(imageVector = EvaIcons.Fill.Trash, contentDescription = "Delete member")
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { setShowDialog(true) }) {
                Text("Delete Group")
            }
            Button(onClick = { /* Handle group modification here */}) {
                Text("Save Changes")
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { setShowDialog(false) },
                    title = { Text(text = "Are you sure you want to delete the group?") },
                    text = { Text("This action cannot be undone.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                setShowDialog(false)
                                coroutineScope.launch {
                                    // Handle the delete confirmation here
                                }
                            }
                        ) {
                            Text("Yes, I am sure")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { setShowDialog(false) }) {
                            Text("No, return")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayInviteFields(
    preferences: PreferencesData,
    groupId: Long,
    onUserAdd: () -> Unit
) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 10.dp)
    ) {
        var email by remember { mutableStateOf(TextFieldValue()) }

        // show email input to fill remaining space after button
        TextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text("Add Member", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            placeholder = { Text("user@example.com")},
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Handle action */ }),
            modifier = Modifier
                .padding(end = 10.dp)
                .width(270.dp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            )
        )

        // show button to add user to group
        Button(
            onClick = {
                if (email.text == "") {
                    alertUser("Please, provide the email", context)
                    return@Button
                }
                else {
                    try {
                        val user = BackendService(preferences).getUserByEmail(email.text)
                        BackendService(preferences).addUserToGroup(user.getId(), groupId)
                        alertUser("User in group!", context)
                        email = TextFieldValue(text = "")
                        onUserAdd()
                    } catch (e: ServerException) {
                        alertUser("User not found", context)
                        return@Button
                    }
                }

            }
        ) {
            Text(text = "Add")
        }
    }
}

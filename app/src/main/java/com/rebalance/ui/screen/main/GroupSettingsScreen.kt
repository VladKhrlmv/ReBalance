package com.rebalance.ui.screen.main

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.backend.dto.AddUserToGroupResult
import com.rebalance.backend.dto.SpendingDeptor
import com.rebalance.backend.localdb.entities.Group
import com.rebalance.backend.service.BackendService
import com.rebalance.util.alertUser
import com.rebalance.util.currencyRegex
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Plus
import compose.icons.evaicons.fill.Trash
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSettingsScreen(
    context: Context,
    groupId: Long,
) {
    val backendService = remember { BackendService.get() }
    val groupSettingsScreenScope = rememberCoroutineScope()

    var group by remember { mutableStateOf<Group?>(null) }
    var groupMembers by remember { mutableStateOf(listOf<SpendingDeptor>()) }

    var groupName by remember { mutableStateOf("") }
    var groupCurrency by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val focusManager: FocusManager = LocalFocusManager.current
    var showDialog by remember { mutableStateOf(false) }

    var addUserResult by remember { mutableStateOf(AddUserToGroupResult.Placeholder) }

    fun updateGroupMembers() {
        groupSettingsScreenScope.launch {
            group = backendService.getGroupById(groupId)
            groupMembers = backendService.getUsersOfGroup(groupId)
            groupName = group?.name ?: ""
            groupCurrency = group?.currency ?: ""
        }
    }

    // fetch group and members
    LaunchedEffect(Unit, addUserResult) {
        updateGroupMembers()
    }

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
                .verticalScroll(scrollState)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
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
                onUserAdd = { email ->
                    groupSettingsScreenScope.launch {
                        addUserResult = backendService.addUserToGroup(groupId, email)
                    }
                }
            )
            when (addUserResult) {
                AddUserToGroupResult.Placeholder -> {}
                AddUserToGroupResult.Added -> {
                    alertUser("Added", context)
                    addUserResult = AddUserToGroupResult.Placeholder
                }
                AddUserToGroupResult.UserNotFound -> {
                    alertUser("User not found", context)
                    addUserResult = AddUserToGroupResult.Placeholder
                }
                AddUserToGroupResult.UserInGroup -> {
                    alertUser("User already in group", context)
                    addUserResult = AddUserToGroupResult.Placeholder
                }
                else -> {
                    alertUser("Please try again", context)
                    addUserResult = AddUserToGroupResult.Placeholder
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 5.dp)
            ) {
                Text(
                    text = "Members",
                    fontSize = 24.sp
                )
            }
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 350.dp),
                state = rememberLazyListState()
            ) {
                items(items = groupMembers, itemContent = { member ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {
                        Text(text = member.nickname)
                        IconButton(onClick = { /* Handle member deletion */ }) {
                            Icon(
                                imageVector = EvaIcons.Fill.Trash,
                                contentDescription = "Delete member"
                            )
                        }
                    }
                })
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { showDialog = true }) {
                Text("Delete Group")
            }
            Button(onClick = { /* Handle group modification here */ }) {
                Text("Save Changes")
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Are you sure you want to delete the group?") },
                    text = { Text("This action cannot be undone.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                groupSettingsScreenScope.launch {
                                    // Handle the delete confirmation here
                                }
                            }
                        ) {
                            Text("Yes, I am sure")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
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
    onUserAdd: (String) -> Unit
) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        var email by remember { mutableStateOf("") }

        // show email input to fill remaining space after button
        TextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text("Add Member", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            placeholder = { Text("user@example.com")},
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Handle action */ }),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.weight(1f)
        )

        // show button to add user to group
        Icon(
            imageVector = EvaIcons.Fill.Plus,
            contentDescription = "Add",
            modifier = Modifier
                .size(30.dp)
                .clickable {
                if (email.isEmpty()) { //TODO: add validation of email
                    alertUser("Please, provide the email", context)
                } else {
                    onUserAdd(email)
                }
            }
        )
    }
}

package com.rebalance.ui.screen.main

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.exceptions.ServerException
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.main.BarChart
import com.rebalance.ui.component.main.GroupSelection
import com.rebalance.ui.component.main.GroupSpendingList
import com.rebalance.util.alertUser

@Composable
fun GroupScreen(
    context: Context
) {
    val preferences = rememberSaveable { Preferences(context).read() }

    // initialize tabs
    val tabItems = listOf("Visual", "List")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) } // selected index of tab
    var groupId by rememberSaveable { mutableStateOf(-1L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        // selection of groups
        DisplayGroupSelection(context, preferences, groupId) { newGroupId ->
            groupId = newGroupId
        }

        // content
        if (selectedTabIndex == 0) { // if visual tab
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (groupId != -1L) { // if group selected, show invite field
                    DisplayInviteFields(
                        preferences,
                        groupId,
                        onUserAdd = {
                            // set group id to -1 and back to start recomposing with updated data
                            val prevGroupId = groupId
                            groupId = -1L
                            groupId = prevGroupId
                        }
                    )
                }

                // show bar chart
                DisplayVisual(preferences, groupId)
            }
        } else { // if list tab
            // show list
            DisplayGroupList(
                BackendService(preferences).getGroupList(groupId),
                preferences,
                groupId,
                context,
                refreshAndOpenGroup = { newGroupId ->
                    groupId = -1L
                    groupId = newGroupId
                }
            )
        }
    }
}


@Composable
private fun DisplayTabs(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex
    ) {
        tabs.forEachIndexed { tabIndex, tab ->
            Tab(
                selected = selectedTabIndex == tabIndex,
                onClick = { onTabClick(tabIndex) },
                text = { Text(tab) },
                modifier = Modifier
                    .height(45.dp)
            )
        }
    }
}

@Composable
private fun DisplayGroupSelection(
    context: Context,
    preferences: PreferencesData,
    groupId: Long,
    onSwitch: (Long) -> Unit
) {
    var showAddGroupDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("groupSelectionGroupScreen"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // show group selection to fill remaining space after button
        GroupSelection(
            preferences,
            if (groupId == -1L) "" else BackendService(preferences).getGroupById(groupId).getName(),
            Modifier
                .padding(start = 10.dp)
                .weight(1f),
            Modifier,
            onSwitch
        )

        // show button to create new group
        Button(
            onClick = {
                showAddGroupDialog = true
            },
            modifier = Modifier
                .padding(10.dp)
                .width(100.dp)
        ) {
            Text(text = "Create")
        }
    }

    // if button create group pressed, show dialog
    if (showAddGroupDialog) {
        Dialog(
            onDismissRequest = {
                showAddGroupDialog = false
            },
        ) {
            Surface(
                shadowElevation = 4.dp
            ) {
                AddGroupScreen(
                    context,
                    onCancel = {
                        showAddGroupDialog = false
                    },
                    onCreate = { groupId ->
                        onSwitch(groupId)
                    }
                )
            }
        }
    }
}

@Composable
private fun DisplayInviteFields(
    preferences: PreferencesData,
    groupId: Long,
    onUserAdd: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var email by remember { mutableStateOf(TextFieldValue()) }

        // show email input to fill remaining space after button
        TextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
            label = {
                Text(text = "Email")
            }
        )

        // show button to add user to group
        Button(
            onClick = {
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
            },
            modifier = Modifier
                .padding(10.dp)
                .width(100.dp)
        ) {
            Text(text = "Invite")
        }
    }
}

@Composable
private fun DisplayVisual(
    preferences: PreferencesData,
    groupId: Long,
) {
    val data = BackendService(preferences).getGroupVisualBarChart(groupId)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Center
    ) {
        BarChart(data)
    }
}

@Composable
private fun DisplayGroupList(
    data: List<Expense>,
    preferences: PreferencesData,
    groupId: Long,
    context: Context,
    refreshAndOpenGroup: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("groupList"),
        contentAlignment = Center
    ) {
        GroupSpendingList(
            data,
            preferences,
            groupId,
            context,
            refreshAndOpenGroup
        )
    }
}

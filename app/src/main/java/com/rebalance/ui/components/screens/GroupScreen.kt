package com.rebalance.ui.components.screens

import android.os.StrictMode
import android.widget.Toast
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.rebalance.backend.api.jsonToApplicationUser
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.api.RequestsSender
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.exceptions.ServerException
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.components.BarChart

@Composable
fun GroupScreen(
    context: Context
) {
    val preferences = rememberSaveable { Preferences(context).read() }

    // initialize tabs
    val tabItems = listOf("Visual", "List")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) } // selected index of tab
    var groupId by rememberSaveable { mutableStateOf(-1L) }
    var userAddedSwitcher by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        DisplayGroupSelection(context, preferences, groupId) { newGroupId ->
            groupId = newGroupId
        }

        // content
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (selectedTabIndex == 0) { // if visual tab
                DisplayVisual(preferences, groupId, userAddedSwitcher) {
                    userAddedSwitcher = !userAddedSwitcher
                }
            } else { // if list tab
                DisplayList(preferences, groupId, BackendService(preferences).getGroupList(groupId))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DisplayGroupSelection(
    context: Context,
    preferences: PreferencesData,
    groupId: Long,
    onSwitch: (Long) -> Unit
) {
    var expandedDropdownGroups by remember { mutableStateOf(false) }
    val addGroupDialogController = remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .fillMaxWidth().testTag("groupSelectionGroupScreen")
    ) {
        ExposedDropdownMenuBox(
            expanded = expandedDropdownGroups,
            onExpandedChange = {
                expandedDropdownGroups = !expandedDropdownGroups
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
        ) {
            TextField(
                value = if(groupId == -1L) "" else BackendService(preferences).getGroupById(groupId).getName(),
                onValueChange = { },
                readOnly = true,
                label = {
                    Text(text = "Group")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedDropdownGroups
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 100.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedDropdownGroups,
                onDismissRequest = { expandedDropdownGroups = false },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val groupList = BackendService(preferences).getGroups().filter { group -> group.getId() != preferences.groupId }
                groupList.forEach { group ->
                    DropdownMenuItem(
                        onClick = {
                            onSwitch(group.getId())
                            expandedDropdownGroups = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(text = group.getName())
                    }
                }
            }
        }
        Button(
            onClick = {
                addGroupDialogController.value = !addGroupDialogController.value
            },
            modifier = Modifier
                .padding(10.dp)
                .align(CenterEnd)
        ) {
            Text(text = "Create")
        }
    }
    if (addGroupDialogController.value) {
        Dialog(
            onDismissRequest = { addGroupDialogController.value = !addGroupDialogController.value },

        ) {
            Surface(
                elevation = 4.dp
            ) {
                AddGroupScreen(context, addGroupDialogController) { groupId ->
                    onSwitch(groupId)
                }
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var email by remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 100.dp),
            label = {
                Text(text = "Email")
            }
        )
        Button(
            onClick = {
                if(groupId == -1L){
                    ContextCompat.getMainExecutor(context).execute {
                        Toast.makeText(
                            context,
                            "Choose a group!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@Button
                }
                try{
                    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                    StrictMode.setThreadPolicy(policy)
                    val getUserByEmailResponse = RequestsSender.sendGet("http://${preferences.serverIp}/users/email/${email.text}")
                    val user = jsonToApplicationUser(getUserByEmailResponse)
                    RequestsSender.sendPost(
                        "http://${preferences.serverIp}/users/${user.getId()}/groups",
                        "{\"id\": ${groupId}}"
                    )
                    ContextCompat.getMainExecutor(context).execute {
                        Toast.makeText(
                            context,
                            "User in group",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    email = TextFieldValue(text = "")
                    onUserAdd()
                }
                catch(e: ServerException){
                    ContextCompat.getMainExecutor(context).execute {
                        Toast.makeText(
                            context,
                            "User not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@Button
                }
            },
            modifier = Modifier
                .padding(10.dp)
                .align(CenterEnd)
        ) {
            Text(text = "Invite")
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
private fun DisplayVisual(
    preferences: PreferencesData,
    groupId: Long,
    userAdded: Boolean,
    onUserAdd: () -> Unit
) {
    val data = BackendService(preferences).getGroupVisualBarChart(groupId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState(),
                flingBehavior = null // TODO: disable
            )
    ) {
        DisplayInviteFields(preferences, groupId, onUserAdd)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally),
            contentAlignment = Center
        ) {
            BarChart(data)
        }
    }
}

@Composable
private fun DisplayList(
    preferences: PreferencesData,
    groupId: Long,
    data: List<Expense>
) {
    Box(
        modifier = Modifier
            .fillMaxSize().testTag("groupList"),
        contentAlignment = Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Top
        ) {
            items(items = data, itemContent = { item ->
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(Color.LightGray)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.getDescription(),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                    Text(
                        text = "${item.getAmount()} ${BackendService(preferences).getGroupById(groupId).getCurrency()}",
                        fontSize = 14.sp,
                        color = Color.hsl(358f, 0.63f, 0.49f),
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }
            })
        }
    }
}

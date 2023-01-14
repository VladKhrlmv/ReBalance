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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.rebalance.backend.api.jsonToApplicationUser
import com.rebalance.backend.api.sendGet
import com.rebalance.backend.api.sendPost
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.exceptions.ServerException
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.BarChartData
import com.rebalance.ui.components.BarChart

@Composable
fun GroupScreen(
    context: Context
) {
    val preferences = rememberSaveable { Preferences(context).read() }

    // initialize tabs
    val tabItems = listOf("Visual", "List")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) } // selected index of tab
    val groupId = rememberSaveable { mutableStateOf(-1L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        DisplayGroupSelection(context, preferences, groupId)

        // content
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (selectedTabIndex == 0) { // if visual tab
                DisplayVisual(preferences, groupId.value, BackendService(preferences).getGroupVisualBarChart(groupId.value))
            } else { // if list tab
                DisplayList(BackendService(preferences).getGroupList(groupId.value))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DisplayGroupSelection(
    context: Context,
    preferences: PreferencesData,
    groupId: MutableState<Long>,
) {
    var expandedDropdownGroups by remember { mutableStateOf(false) }
    var groupName by rememberSaveable { mutableStateOf("") }
    val addGroupDialogController = remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .fillMaxWidth()
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
                value = groupName,
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
                            groupName = group.getName()
                            groupId.value = group.getId()
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
                AddGroupScreen(context, addGroupDialogController)
            }
        }
    }
}

@Composable
private fun DisplayInviteFields(
    preferences: PreferencesData,
    groupId: Long
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
                    val getUserByEmailResponse = sendGet("http://${preferences.serverIp}/users/email/${email.text}")
                    val user = jsonToApplicationUser(getUserByEmailResponse)
                    sendPost(
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
    data: List<BarChartData>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState(),
                flingBehavior = null // TODO: disable
            )
    ) {
        DisplayInviteFields(preferences, groupId)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Center
        ) {
            BarChart(data)
        }

        Text(
            text = "Balance",
            fontSize = 30.sp,
            modifier = Modifier
                .padding(20.dp, 20.dp, 0.dp, 20.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Top
        ) {
            for (item in data) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(Color.LightGray)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.debtor,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                    Text(
                        text = item.value.toString() + " PLN",
                        fontSize = 14.sp,
                        color = Color.hsl(358f, 0.63f, 0.49f),
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DisplayList(
    data: List<Expense>
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
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
                        //todo change from PLN to group currency
                        text = (item.getAmount()).toString() + " PLN",
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

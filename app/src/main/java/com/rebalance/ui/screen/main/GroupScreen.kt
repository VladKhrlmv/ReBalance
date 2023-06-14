package com.rebalance.ui.screen.main

import android.content.Context
import android.graphics.BitmapFactory
import android.os.StrictMode
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.api.RequestsSender
import com.rebalance.backend.api.jsonToApplicationUser
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.exceptions.ServerException
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.main.BarChart
import com.rebalance.ui.component.main.GroupSelection
import com.rebalance.utils.alertUser
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Trash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                    DisplayInviteFields(preferences, groupId) {
                        // set group id to -1 and back to start recomposing with updated data
                        val prevGroupId = groupId
                        groupId = -1L
                        groupId = prevGroupId
                    }
                }

                // show bar chart
                DisplayVisual(preferences, groupId)
            }
        } else { // if list tab
            // show list
            DisplayList(
                preferences,
                groupId,
                BackendService(preferences).getGroupList(groupId),
                refreshAndOpenGroup = { groupId = it },
                context
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
    val addGroupDialogController = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("groupSelectionGroupScreen")
    ) {
        GroupSelection(
            preferences,
            if (groupId == -1L) "" else BackendService(preferences).getGroupById(groupId).getName(),
            onSwitch
        )
        Button(
            onClick = {
                addGroupDialogController.value = !addGroupDialogController.value
            },
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterEnd)
        ) {
            Text(text = "Create")
        }
    }
    if (addGroupDialogController.value) {
        Dialog(
            onDismissRequest = { addGroupDialogController.value = !addGroupDialogController.value },

            ) {
            Surface(
                shadowElevation = 4.dp
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
                try {
                    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                    StrictMode.setThreadPolicy(policy)
                    val getUserByEmailResponse =
                        RequestsSender.sendGet("http://${preferences.serverIp}/users/email/${email.text}")
                    val user = jsonToApplicationUser(getUserByEmailResponse)
                    RequestsSender.sendPost(
                        "http://${preferences.serverIp}/users/${user.getId()}/groups",
                        "{\"id\": ${groupId}}"
                    )
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
                .align(CenterEnd)
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
private fun DisplayList(
    preferences: PreferencesData,
    groupId: Long,
    data: List<Expense>,
    refreshAndOpenGroup: (Long) -> Unit,
    context: Context
) {
    val groupCurrency =
        if (groupId == -1L) "" else BackendService(preferences).getGroupById(groupId).getCurrency()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("groupList"),
        contentAlignment = Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Top
        ) {
            val expensesByGlobalId: Map<Long?, List<Expense>> = data.groupBy { it.getGlobalId() }
            items(items = data.filter { it.getAmount() >= 0 }, itemContent = { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.getDescription(),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(5.dp)
                            )
                            Text(
                                text = "${item.getAmount()} $groupCurrency",
                                fontSize = 14.sp,
                                color = Color.hsl(358f, 0.63f, 0.49f),
                                modifier = Modifier
                                    .padding(5.dp)
                            )

                            val showDialog = remember { mutableStateOf(false) }

                            IconButton(onClick = {
                                showDialog.value = true
                            }) {
                                Icon(EvaIcons.Fill.Trash, "Delete expense")
                            }
                            if (showDialog.value) {
                                AlertDialog(
                                    onDismissRequest = { showDialog.value = false },
                                    title = { Text("Confirmation") },
                                    text = { Text("Are you sure you want to delete this expense?") },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            BackendService(preferences).deleteExpenseByGlobalId(item.getGlobalId())
                                            alertUser("Expense deleted!", context)
                                            showDialog.value = false
                                            refreshAndOpenGroup(-1L)
                                            refreshAndOpenGroup(groupId)
                                        }) {
                                            Text("Yes")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDialog.value = false }) {
                                            Text("No")
                                        }
                                    }
                                )
                            }
                        }
                        Text(
                            text = "Category: " + item.getCategory(),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                        )
                        Text(
                            text = "Date: " + item.getDateStamp(),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                        )
                        val payer = item.getUser()
                        if (payer != null) {
                            Text(
                                text = "Payed by: " + payer.getUsername(),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                            )
                        }
                        val toWhom = expensesByGlobalId[item.getGlobalId()]
                        if (toWhom != null && toWhom.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .background(Color.White)
                            ) {
                                Text(
                                    text = "To:",
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .fillMaxWidth()
                                )
                                for (expenseWithUser in toWhom.filter { it.getAmount() <= 0 }) {
                                    val user = expenseWithUser.getUser()
                                    if (user != null) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = user.getUsername(),
                                                fontSize = 14.sp,
                                                modifier = Modifier
                                                    .padding(horizontal = 10.dp)
                                            )
                                            Text(
                                                text = "${expenseWithUser.getAmount()} $groupCurrency",
                                                fontSize = 14.sp,
                                                modifier = Modifier
                                                    .padding(horizontal = 10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        val coroutineScope = rememberCoroutineScope()
                        val imgBase64 =
                            BackendService(preferences).getExpensePicture(item.getGlobalId())
                        val imageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }

                        DisposableEffect(Unit) {
                            if (imgBase64 != null && imageBitmap.value == null) {
                                coroutineScope.launch {
                                    val bitmap = withContext(Dispatchers.IO) {
                                        BitmapFactory.decodeByteArray(
                                            imgBase64,
                                            0,
                                            imgBase64.size
                                        ).asImageBitmap()
                                    }
                                    imageBitmap.value = bitmap
                                }
                            }
                            onDispose { }
                        }

                        if (imageBitmap.value != null) {
                            Image(
                                bitmap = imageBitmap.value!!,
                                contentDescription = "Image",
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            })
        }
    }
}

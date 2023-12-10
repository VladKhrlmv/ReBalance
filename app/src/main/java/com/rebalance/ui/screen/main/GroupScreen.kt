package com.rebalance.ui.screen.main

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.rebalance.backend.dto.BarChartItem
import com.rebalance.backend.dto.DeleteResult
import com.rebalance.backend.localdb.entities.Group
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.main.*
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateSingleTo
import com.rebalance.util.alertUser
import kotlinx.coroutines.launch


@Composable
fun GroupScreen(
    context: Context,
    navHostController: NavHostController,
    setOnPlusClick: (() -> Unit) -> Unit
) {
    val backendService = remember { BackendService.get() }
    val groupScope = rememberCoroutineScope()

    val tabItems = listOf("Visual", "List")
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    var groupId by rememberSaveable { mutableLongStateOf(-1L) }
    var group by remember { mutableStateOf<Group?>(null) }

    var barChartData by remember { mutableStateOf(listOf<BarChartItem>()) }

    var deleteResult by remember { mutableStateOf(DeleteResult.Placeholder) }

    val focusManager: FocusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        setOnPlusClick {
            navigateSingleTo(navHostController, Routes.AddSpending)
        }
    }

    LaunchedEffect(groupId) {
        if (groupId != -1L) { // if group is selected
            group = backendService.getGroupById(groupId)
            if (selectedTabIndex == 0) { // if tab is visual
                barChartData = backendService.getBarChartData(groupId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { /* Do nothing on press to avoid ripple effect */
                    },
                    onTap = { focusManager.clearFocus() }
                )
            }
    ) {
        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        // selection of groups
        DisplayGroupSelection(
            context,
            backendService,
            navHostController,
            group,
            onSwitch = { newGroupId ->
                groupId = newGroupId
            }
        )

        if (selectedTabIndex == 0) { // if visual tab
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                BarChart(barChartData)
                ExpenseDistribution(group, barChartData)
            }
        } else { // if list tab
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("groupList"),
                contentAlignment = Center
            ) {
                if (groupId != -1L && group != null) {
                    GroupSpendingList(
                        group!!,
                        context,
                        onDelete = {
                            groupScope.launch {
                                deleteResult = backendService.deleteGroupExpenseById(it)
                            }
                        }
                    )
                }
            }
        }

        when (deleteResult) {
            DeleteResult.Placeholder -> {}
            DeleteResult.Deleted -> {
                alertUser("Deleted", context)
                deleteResult = DeleteResult.Placeholder
            }
            DeleteResult.NotFound -> {
                alertUser("Not found expense, please try again", context)
                deleteResult = DeleteResult.Placeholder
            }
            else -> {
                alertUser("unexpected error occurred", context)
                deleteResult = DeleteResult.Placeholder
            }
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
    backendService: BackendService,
    navHostController: NavHostController,
    group: Group?,
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
            group,
            Modifier
                .padding(start = 10.dp)
                .weight(1f)
                .width(300.dp),
            Modifier
                .fillMaxWidth(),
            onSwitch
        )

        GroupContextMenu(navHostController, group?.id ?: -1L, onCreateGroupClick = {
            showAddGroupDialog = true
        })
    }

    // if button create group pressed, show dialog
    if (showAddGroupDialog) {
        Dialog(
            onDismissRequest = {
                showAddGroupDialog = false
            },
        ) {
            AddGroupScreen(
                context,
                backendService,
                onClose = {
                    showAddGroupDialog = false
                },
                onCreate = { groupId ->
                    showAddGroupDialog = false
                    onSwitch(groupId)
                }
            )
        }
    }
}

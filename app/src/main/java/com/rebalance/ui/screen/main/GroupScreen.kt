package com.rebalance.ui.screen.main

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.main.*
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateSingleTo


@Composable
fun GroupScreen(
    context: Context,
    navHostController: NavHostController,
    setOnPlusClick: (() -> Unit) -> Unit
) {
    val preferences = rememberSaveable { Preferences(context).read() }
    // initialize tabs
    val tabItems = listOf("Visual", "List")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) } // selected index of tab
    var groupId = rememberSaveable { mutableStateOf(-1L) }

    LaunchedEffect(Unit) {
        setOnPlusClick {
            navigateSingleTo(navHostController, Routes.AddSpending)
        }
    }
    println("Set for group")

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        // selection of groups
        DisplayGroupSelection(context, preferences, navHostController, groupId.value) { newGroupId ->
            groupId.value = newGroupId
        }

        // content
        if (selectedTabIndex == 0) { // if visual tab
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // show bar chart
                DisplayVisual(preferences, groupId.value)
            }
        } else { // if list tab
            // show list
            DisplayGroupList(
                BackendService(preferences).getGroupList(groupId.value),
                preferences,
                groupId.value,
                context,
                refreshAndOpenGroup = { newGroupId ->
                    groupId.value = -1L
                    groupId.value = newGroupId
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
    navHostController: NavHostController,
    groupId: Long,
    onSwitch: (Long) -> Unit
) {
    var showAddGroupDialog = remember { mutableStateOf(false) }

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
                .weight(1f)
                .width(300.dp),
            Modifier
                .fillMaxWidth(),
            onSwitch
        )

        GroupContextMenu(navHostController, showAddGroupDialog, groupId)
    }

    // if button create group pressed, show dialog
    if (showAddGroupDialog.value) {
        Dialog(
            onDismissRequest = {
                showAddGroupDialog.value = false
            },
        ) {
            AddGroupScreen(
                context,
                onCancel = {
                    showAddGroupDialog.value = false
                },
                onCreate = { groupId ->
                    onSwitch(groupId)
                }
            )
        }
    }
}

@Composable
private fun DisplayVisual(
    preferences: PreferencesData,
    groupId: Long,
) {
    val data = BackendService(preferences).getGroupVisualBarChart(groupId)
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Center
        ) {
            BarChart(data)
        }
        ExpenseDistribution(preferences, groupId)
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

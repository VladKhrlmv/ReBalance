package com.rebalance.ui.screen.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rebalance.backend.dto.DeleteResult
import com.rebalance.backend.dto.ScaleItem
import com.rebalance.backend.dto.ScaledDateItem
import com.rebalance.backend.dto.SumByCategoryItem
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.main.ExpandableList
import com.rebalance.ui.component.main.scaffold.PieChart
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateSingleTo
import com.rebalance.util.alertUser
import kotlinx.coroutines.launch

@Composable
fun PersonalScreen(
    context: Context,
    pieChartActive: MutableState<Boolean>,
    navHostController: NavHostController,
    setOnPlusClick: (() -> Unit) -> Unit
) {
    val backendService = remember { BackendService.get() }
    val personalScope = rememberCoroutineScope()

    val scaleItems = remember { backendService.getScaleItems() }
    var selectedScaleIndex by remember { mutableIntStateOf(0) }

    var tabItems by remember { mutableStateOf(listOf<ScaledDateItem>()) }
    var selectedTabIndex by remember { mutableIntStateOf(-1) }

    val openCategory = remember { mutableStateOf("") }
    val expandableListState = rememberLazyListState()

    var sumByCategories by remember { mutableStateOf(listOf<SumByCategoryItem>()) }

    var deleteResult by remember { mutableStateOf(DeleteResult.Placeholder) }

    LaunchedEffect(Unit) {
        setOnPlusClick {
            navigateSingleTo(navHostController, Routes.AddSpending)
        }
    }

    LaunchedEffect(selectedScaleIndex) { // get tabs on scale change
        tabItems = backendService.getTabItems(scaleItems[selectedScaleIndex].type)
        selectedTabIndex = (tabItems.size - 1)
        if (selectedTabIndex != -1) {
            sumByCategories = backendService.getPieChartData(tabItems[selectedTabIndex])
        }
    }

    LaunchedEffect(selectedTabIndex) {// get new values on tab change
        if (selectedTabIndex != -1) {
            sumByCategories = backendService.getPieChartData(tabItems[selectedTabIndex])
        }
    }
    LaunchedEffect(deleteResult) { // get new values on delete
        if (deleteResult == DeleteResult.Deleted && selectedTabIndex != -1) {
            sumByCategories = backendService.getPieChartData(tabItems[selectedTabIndex])
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // top tabs
        if (selectedTabIndex != -1) {
            DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
                selectedTabIndex = tabIndex
            }
        }

        // content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            // scale buttons
            DisplayScaleButtons(
                scaleItems, selectedScaleIndex,
            ) { scaleIndex ->
                selectedScaleIndex = scaleIndex
            }

            // display pie chart or list
            if (pieChartActive.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .width(200.dp)
                        .height(200.dp),
                    contentAlignment = Center
                ) {
                    PieChart(sumByCategories, pieChartActive, openCategory, expandableListState)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("personalList"),
                    contentAlignment = TopCenter
                ) {
                    if (selectedTabIndex != -1) {
                        ExpandableList(
                            items = sumByCategories,
                            context,
                            openCategory,
                            tabItems[selectedTabIndex],
                            expandableListState,
                            deleteItem = {
                                personalScope.launch {
                                    deleteResult = backendService.deletePersonalExpenseById(it)
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
}

@Composable
private fun DisplayTabs(
    tabs: List<ScaledDateItem>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 200.dp
    ) {
        tabs.forEachIndexed { tabIndex, tab ->
            Tab(
                selected = selectedTabIndex == tabIndex,
                onClick = { onTabClick(tabIndex) },
                text = { Text(tab.name) },
                modifier = Modifier
                    .height(45.dp)
                    .width(180.dp)
            )
        }
    }
}


@Composable
private fun DisplayScaleButtons(
    scaleItems: List<ScaleItem>,
    selectedScaleIndex: Int,
    onButtonClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        scaleItems.forEachIndexed { scaleIndex, scaleItem ->
            TextButton(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .background(
                        if (scaleIndex == selectedScaleIndex) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        shape = CircleShape
                    ),
                onClick = { onButtonClick(scaleIndex) }
            ) {
                Text(text = scaleItem.name)
            }
        }
    }
}


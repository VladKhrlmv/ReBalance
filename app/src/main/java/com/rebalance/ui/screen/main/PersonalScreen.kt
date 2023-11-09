package com.rebalance.ui.screen.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.ExpenseItem
import com.rebalance.backend.service.ScaleItem
import com.rebalance.backend.service.ScaledDateItem
import com.rebalance.ui.component.main.ExpandableList
import com.rebalance.ui.component.main.scaffold.PieChart
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateSingleTo

@Composable
fun PersonalScreen(
    context: Context,
    pieChartActive: MutableState<Boolean>,
    navHostController: NavHostController,
    setOnPlusClick: (() -> Unit) -> Unit
) {
    val preferences = rememberSaveable { Preferences(context).read() }

    // initialize scale variables
    val scaleItems = BackendService(preferences).getScaleItems() // list of scales
    var selectedScaleIndex by rememberSaveable { mutableStateOf(0) } // selected index of scale

    // initialize tabs
    val tabItems = rememberSaveable { mutableListOf<ScaledDateItem>() } // list of tabs
    var selectedTabIndex by rememberSaveable { mutableStateOf(Int.MAX_VALUE) } // selected index of tab
    var openCategory = rememberSaveable { mutableStateOf("") }
    val expandableListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        setOnPlusClick{
            navigateSingleTo(navHostController, Routes.AddSpending)
        }
    }

    // declare function to update tab items
    fun updateTabItems(
        type: String
    ) {
        tabItems.clear()
        tabItems.addAll(BackendService(preferences).getScaledDateItems(type))
        if (selectedTabIndex >= tabItems.size) selectedTabIndex = tabItems.size - 1
    }
    // fill initial tabs
    updateTabItems(scaleItems[selectedScaleIndex].type)
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
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
//                personalViewModel.updateTabItems(scaleItem.type)
                updateTabItems(scaleItems[selectedScaleIndex].type)
                selectedTabIndex = (tabItems.size - 1)
            }

            // pie chart or list
            // initialize data
            val data = rememberSaveable {
                mutableListOf<ExpenseItem>()
            }

            // declare function to update data
            fun updateData() {
                updateTabItems(scaleItems[selectedScaleIndex].type)
                data.clear()
                data.addAll(
                    BackendService(preferences).getPersonal(
                        tabItems[selectedTabIndex].dateFrom,
                        tabItems[selectedTabIndex].dateTo
                    )
                )
            }
            // fill initial data
            updateData()

            // display pie chart or list
            if (pieChartActive.value) {
                DisplayPieChart(data, pieChartActive, openCategory, expandableListState)
            } else {
                DisplayList(
                    data,
                    preferences,
                    openCategory,
                    expandableListState,
                    updateData = {
                        updateData()
                    }
                )
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
        edgePadding = 110.dp
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

@Composable
private fun DisplayPieChart(
    data: List<ExpenseItem>,
    pieChartActive: MutableState<Boolean>,
    openCategory: MutableState<String>,
    expandableListState: LazyListState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .width(200.dp)
            .height(200.dp),
        contentAlignment = Center
    ) {
        PieChart(data, pieChartActive, openCategory, expandableListState)
    }
}

@Composable
private fun DisplayList(
    data: List<ExpenseItem>,
    preferences: PreferencesData,
    openCategory: MutableState<String>,
    expandableListState: LazyListState,
    updateData: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("personalList"),
        contentAlignment = TopCenter
    ) {
        ExpandableList(
            items = data,
            preferences,
            LocalContext.current,
            openCategory,
            updateData,
            expandableListState)
    }
}

package com.rebalance.ui.screen.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.ExpenseItem
import com.rebalance.backend.service.ScaleItem
import com.rebalance.backend.service.ScaledDateItem
import com.rebalance.ui.component.main.ExpandableList
import com.rebalance.ui.component.main.scaffold.PieChart

@Composable
fun PersonalScreen(
    context: Context,
    pieChartActive: Boolean
) {
    val preferences = rememberSaveable { Preferences(context).read() }

    // initialize scale variables
    val scaleItems = BackendService(preferences).getScaleItems() // list of scales
    var selectedScaleIndex by rememberSaveable { mutableStateOf(0) } // selected index of scale

    // initialize tabs
    val tabItems = rememberSaveable { mutableListOf<ScaledDateItem>() } // list of tabs

    // declare function to update tab items
    fun updateTabItems(
        type: String
    ) {
        tabItems.clear()
        tabItems.addAll(BackendService(preferences).getScaledDateItems(type))
    }
    // fill initial tabs
    updateTabItems(scaleItems[selectedScaleIndex].type)
    var selectedTabIndex by rememberSaveable { mutableStateOf(tabItems.size - 1) } // selected index of tab

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
            if (pieChartActive) {
                DisplayPieChart(data)
            } else {
                DisplayList(
                    data,
                    preferences,
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
                        if (scaleIndex == selectedScaleIndex) Color.Blue.copy(alpha = 0.5f) else Color.Transparent, //TODO: change to theme color
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
    data: List<ExpenseItem>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .width(200.dp)
            .height(200.dp),
        contentAlignment = Center
    ) {
        PieChart(data)
    }
}

@Composable
private fun DisplayList(
    data: List<ExpenseItem>,
    preferences: PreferencesData,
    updateData: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("personalList"),
        contentAlignment = TopCenter
    ) {
        ExpandableList(items = data, preferences, LocalContext.current, updateData)
    }
}

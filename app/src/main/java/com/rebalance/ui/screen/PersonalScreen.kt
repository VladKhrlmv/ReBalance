package com.rebalance.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.ExpenseItem
import com.rebalance.backend.service.ScaleItem
import com.rebalance.backend.service.ScaledDateItem
import com.rebalance.ui.component.ExpandableList
import com.rebalance.ui.component.PieChart

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
    updateTabItems(preferences, tabItems, scaleItems[selectedScaleIndex].type)
    var selectedTabIndex by rememberSaveable { mutableStateOf(tabItems.size - 1) } // selected index of tab
    val scaleButtonWidth = 50
    val scaleButtonPadding = 8

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        // content
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val data = BackendService(preferences).getPersonal(
                tabItems[selectedTabIndex].dateFrom,
                tabItems[selectedTabIndex].dateTo
            )
            if (pieChartActive) {
                DisplayPieChart(data)
            } else {
                DisplayList(
                    scaleButtonWidth, scaleButtonPadding, data, preferences
                )
            }

            // scale buttons
            DisplayScaleButtons(
                scaleItems, selectedScaleIndex, scaleButtonWidth, scaleButtonPadding
            ) { scaleIndex ->
                selectedScaleIndex = scaleIndex
//                personalViewModel.updateTabItems(scaleItem.type)
                updateTabItems(preferences, tabItems, scaleItems[selectedScaleIndex].type)
                selectedTabIndex = (tabItems.size - 1)
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
    ScrollableTabRow( // TODO: make it lazy (LazyRow with horizontal scroll and stitching)
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
    scaleButtonWidth: Int,
    scaleButtonPadding: Int,
    onButtonClick: (Int) -> Unit
) {
    Column( //TODO: move to function
        modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center
    ) {
        scaleItems.forEachIndexed { scaleIndex, scaleItem ->
            TextButton(
                modifier = Modifier
                    .padding(scaleButtonPadding.dp, 5.dp, 0.dp, 5.dp)
                    .width(scaleButtonWidth.dp)
                    .height(50.dp)
                    .drawWithContent {
                        drawContent()

                        if (selectedScaleIndex == scaleIndex) {
                            val strokeWidth = Stroke.DefaultMiter * 2

                            drawLine(
                                brush = SolidColor(Color.Black),
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Square,
                                start = Offset.Zero,
                                end = Offset(0f, size.height)
                            )
                        }
                    },
                onClick = { onButtonClick(scaleIndex) }
                //colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
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
    scaleButtonWidth: Int,
    scaleButtonPadding: Int,
    data: List<ExpenseItem>,
    preferences: PreferencesData
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding((scaleButtonWidth + scaleButtonPadding).dp, 0.dp, 0.dp, 0.dp) //TODO: change to offset()
            .testTag("personalList"),
        contentAlignment = TopCenter
    ) {
        ExpandableList(items = data, preferences, LocalContext.current)
    }
}

private fun updateTabItems(
    preferences: PreferencesData,
    tabItems: MutableList<ScaledDateItem>,
    type: String
) {
    tabItems.clear()
    tabItems.addAll(BackendService(preferences).getScaledDateItems(type))
}

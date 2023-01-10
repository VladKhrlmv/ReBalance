package com.rebalance.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.*
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.ScaleItem
import com.rebalance.backend.service.ScaledDateItem

@Composable
fun PersonalScreen(
    pieChartActive: Boolean
) {
    // initialize scale variables
    val scaleItems = BackendService().getScaleItems() // list of scales
    var selectedScaleIndex by rememberSaveable { mutableStateOf(0) } // selected index of scale

    // initialize tabs
    val tabItems = rememberSaveable { mutableListOf<ScaledDateItem>() } // list of tabs
    updateTabItems(tabItems, scaleItems[selectedScaleIndex].type)
    var selectedTabIndex by rememberSaveable { mutableStateOf(tabItems.size - 1) } // selected index of tab

    val scaleButtonWidth = 50
    val scaleButtonPadding = 8

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // PieChart() //TODO: fix

        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        // content
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (pieChartActive) {
                DisplayPieChart(tabItems[selectedTabIndex].name)
            } else {
                DisplayList(
                    scaleButtonWidth, scaleButtonPadding, BackendService().getPersonal(
                        scaleItems[selectedScaleIndex].type, tabItems[selectedTabIndex].date, false
                    )
                )
            }

            // scale buttons
            DisplayScaleButtons(
                scaleItems, selectedScaleIndex, scaleButtonWidth, scaleButtonPadding
            ) { scaleIndex ->
                selectedScaleIndex = scaleIndex
//                personalViewModel.updateTabItems(scaleItem.type)
                updateTabItems(tabItems, scaleItems[selectedScaleIndex].type)
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
                onClick = { onButtonClick(scaleIndex) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
            ) {
                Text(text = scaleItem.name)
            }
        }
    }
}

@Composable
private fun DisplayPieChart(
    text: String
) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Center
    ) {
        Box( // TODO: change it to pie chart
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .clip(CircleShape)
                .background(Color.Yellow), contentAlignment = Center
        ) {
            Text(text = text)
        }
    }
}

@Composable
private fun DisplayList(
    scaleButtonWidth: Int,
    scaleButtonPadding: Int,
    list: List<Expense>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding((scaleButtonWidth + scaleButtonPadding).dp, 0.dp, 0.dp, 0.dp),
        contentAlignment = Center
    ) {
        LazyColumn( // TODO: change it to lazy list of spendings
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp), verticalArrangement = Arrangement.Top
        ) {
            items(items = list, itemContent = { item ->
                Text(
                    text = "Item ${item.getDescription()}",
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .background(Color.Blue),
                    fontSize = 19.sp
                )
            })
        }
    }
}

private fun updateTabItems(
    tabItems: MutableList<ScaledDateItem>,
    type: String
) {
    tabItems.clear()
    tabItems.addAll(BackendService().getScaledDateItems(type))
}

@Preview
@Composable
private fun DefaultPreview() {
    PersonalScreen(false)
}

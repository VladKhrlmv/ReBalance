package com.rebalance.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.BarChartData
import com.rebalance.ui.components.BarChart

@Composable
fun GroupScreen() {
    // initialize tabs
    val tabItems = listOf("Visual", "List")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) } // selected index of tab

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        // content
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (selectedTabIndex == 0) { // if visual tab
                DisplayVisual(BackendService().getGroupVisualBarChart())
            } else { // if list tab
                DisplayList(BackendService().getGroupList())
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
private fun DisplayVisual(
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
        Box(
            modifier = Modifier
                .width(200.dp)
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
                        text = (item.getAmount()/100).toString() + " PLN",
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

@Preview
@Composable
private fun DefaultPreview() {
    GroupScreen()
}

package com.rebalance.ui.components.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rebalance.*

@Composable
fun GroupScreen() {
    // initialize tabs
    val tabItems = listOf("Visual", "List")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) } // selected index of tab

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /*Text(
            text = "Group View",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )*/
        // BarChart() // TODO: fix

        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        // content
        Box (
            modifier = Modifier
                .fillMaxSize()
        ){
            if (selectedTabIndex == 0) { // if visual tab
                DisplayVisual()
            }
            else { // if list tab
                DisplayList(DummyBackend().getGroup())
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
private fun DisplayVisual() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState(),
                flingBehavior = null // TODO: disable
            )
    ) {
        Box( // TODO: change to bar chart
            modifier = Modifier
                .width(200.dp)
                .height(400.dp)
                .background(Color.Yellow)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Center
        ) {
            Text(text = "Bars")
        }

        Text(
            text = "Balance",
            fontSize = 30.sp,
            modifier = Modifier
                .padding(20.dp, 20.dp, 0.dp, 20.dp)
        )

        Column ( // TODO: change to list of dropdowns
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Top
        ){
            repeat(10) { item ->
                Text(
                    text = "Item $item",
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .background(Color.Blue),
                    fontSize = 19.sp
                )
            }
        }
    }
}

@Composable
private fun DisplayList(
    list: List<DummyItemValue>
){
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Center
    ) {
        LazyColumn ( // TODO: change it to lazy list of spendings
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Top
        ){
            items(items = list, itemContent = { item ->
                Text(
                    text = "Item ${item.name}",
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .background(Color.Blue),
                    fontSize = 19.sp
                )
            })
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    GroupScreen()
}

package com.rebalance.ui.components.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*

import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PersonalScreen() {
    /*Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Personal View",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }*/

    // working vertical internal navigation
    /*Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        // Creating a Scrollable Box
        Box(modifier = Modifier.background(Color.LightGray).verticalScroll(rememberScrollState()).padding(32.dp)) {
            Column {

                // Create 6 Scrollable Boxes
                repeat(6) {
                    Box(modifier = Modifier.height(128.dp).verticalScroll(rememberScrollState())) {

                        // Creating a Text in each Box
                        Text(
                            "Scroll here",
                            modifier = Modifier
                                .border(12.dp, Color.DarkGray)
                                .padding(24.dp)
                                .height(150.dp)
                        )
                    }
                }
            }
        }
    }*/

    Show()
}

@Composable
fun Show() {
    val listItems: List<ListItem> = listOf(
        ListItem("Jayme", "Jayme"),
        ListItem("Gil", "Gil"),
        ListItem("Juice WRLD", "Juice WRLD"),
        ListItem("Callan", "Callan"),
        ListItem("Braxton", "Braxton"),
        ListItem("Kyla", "Kyla"),
        ListItem("Lil Mosey", "Lil Mosey"),
        ListItem("Allan", "Allan"),
        ListItem("Mike", "Mike"),
        ListItem("Drew", "Drew"),
        ListItem("Nia", "Nia"),
        ListItem("Coi Relay", "Coi Relay")
    )

    var selectedTabIndex by remember { mutableStateOf(0) }

//    Row(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
////            .wrapContentSize(Alignment.Center)
//        ) {
//            repeat(5) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
//                    contentDescription = "user icon",
//                    modifier = Modifier
////                        .padding(horizontal = 8.dp)
//                )
//            }
//        }

        Column(
            modifier = Modifier
                .fillMaxSize()
//            .wrapContentSize(Alignment.Center)
        ) {
            DisplayListHorizontal(listItems, selectedTabIndex) { tabIndex ->
                selectedTabIndex = tabIndex
            }

            ListItem(listItems[selectedTabIndex])
        }
//    }
}

@Composable
fun DisplayListHorizontal(
    tabs: List<ListItem>,
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

// working 2D navigation
@Composable
fun DisplayList(list: List<ListItem>) {
    LazyRow(modifier = Modifier.fillMaxHeight()) {
        items(items = list, itemContent = { item ->
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(items = list, itemContent = { item ->
                    ListItem(item = item)
                })
            }
        })
    }
}

@Composable
fun ListItem(item: ListItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .height(60.dp)
            .background(color = Color.Gray)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "user icon",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(CenterVertically)
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(CenterVertically),
                text = item.name,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

data class ListItem(val name: String, val text: String)


@Preview
@Composable
fun DefaultPreview() {
    Show()
}
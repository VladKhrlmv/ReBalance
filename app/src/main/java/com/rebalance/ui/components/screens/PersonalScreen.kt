package com.rebalance.ui.components.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebalance.*
import com.rebalance.R

@Composable
fun PersonalScreen(
    pieChartActive: Boolean,
    personalViewModel: PersonalViewModel = viewModel()
) {
    // initialize scale variables
    val scaleItems = DummyBackend().getScale() // list of scales
    var selectedScaleIndex by rememberSaveable { mutableStateOf(0) } // selected index of scale

    // initialize tabs
//    val tabItems = personalViewModel.tabItems
    val tabItems = rememberSaveable { mutableListOf<DummyItem>() } // list of tabs
    updateTabItems(tabItems, scaleItems[selectedScaleIndex].type)
    var selectedTabIndex by rememberSaveable { mutableStateOf(tabItems.size - 1) } // selected index of tab

    val scaleButtonWidth = 50
    val scaleButtonPadding = 8

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // top tabs
        DisplayTabs(tabItems, selectedTabIndex) { tabIndex ->
            selectedTabIndex = tabIndex
        }

        // content
        Box (
            modifier = Modifier
                .fillMaxSize()
        ){
            if (pieChartActive) {
                DisplayPieChart(tabItems[selectedTabIndex].name)
            }
            else {
                DisplayList(scaleButtonWidth, scaleButtonPadding, DummyBackend().getPersonal(
                    scaleItems[selectedScaleIndex].type,
                    tabItems[selectedTabIndex].date
                ))
            }

            // scale buttons
            DisplayScaleButtons(scaleItems, selectedScaleIndex, scaleButtonWidth, scaleButtonPadding) { scaleIndex ->
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
    tabs: List<DummyItem>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit
) {
    ScrollableTabRow( // TODO: make it lazy
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
    scaleItems: List<DummyScaleItem>,
    selectedScaleIndex: Int,
    scaleButtonWidth: Int,
    scaleButtonPadding: Int,
    onButtonClick: (Int) -> Unit
)
{
    Column( //TODO: move to function
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        scaleItems.forEachIndexed{ scaleIndex, scaleItem ->
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
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Center
    ) {
        Box( // TODO: change it to pie chart
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .clip(CircleShape)
                .background(Color.Yellow),
            contentAlignment = Center
        ) {
            Text(text = text)
        }
    }
}

@Composable
private fun DisplayList(
    scaleButtonWidth: Int,
    scaleButtonPadding: Int,
    list: List<DummyItemValue>
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding((scaleButtonWidth + scaleButtonPadding).dp, 0.dp, 0.dp, 0.dp),
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

private fun updateTabItems(
    tabItems: MutableList<DummyItem>,
    type: DummyScale
) {
    tabItems.clear()
    tabItems.addAll(DummyBackend().getValues(type))
}

@Preview
@Composable
private fun DefaultPreview() {
    PersonalScreen(false)
}






// -------------------- sample code for lazy rows --------------------

//@Preview
//@Composable
//fun LazyRowPreview(){
//    LazyRowMain();
//}

@Composable
fun LazyRowMain() {
    LazyRowExample(list = DummyBackend().getValues(DummyScale.Year))
}

@Composable
fun LazyRowExample(list: List<DummyItem>) {
    // implement LazyRow
    LazyRow(
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize(1F)
    ) {
        // display items horizontally
        items(items = list, itemContent = { item ->
            ListItem(item = item)
        })
    }
}


@Composable
fun ListItem(item: DummyItem) {
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

package com.rebalance.ui.components.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.R
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

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

      // working vertical internal
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

    val listItems: List<ListItem> = listOf(
        ListItem("Jayme"),
        ListItem("Gil"),
        ListItem("Juice WRLD"),
        ListItem("Callan"),
        ListItem("Braxton"),
        ListItem("Kyla"),
        ListItem("Lil Mosey"),
        ListItem("Allan"),
        ListItem("Mike"),
        ListItem("Drew"),
        ListItem("Nia"),
        ListItem("Coi Relay")
    )
    DisplayList(listItems)
}

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

data class ListItem(val name: String)

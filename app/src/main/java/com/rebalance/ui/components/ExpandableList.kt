package com.rebalance.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.ExpenseItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandableList(items: List<ExpenseItem>, preferences: PreferencesData) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        for (item in items) {
            val expanded = remember { mutableStateOf(false) }
            ListItem(
                text = { Text(item.text) },
                icon = { //TODO: change to category icon
                    CardArrow(expanded.value)
                },
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.amount.toString() + " PLN",
                            fontSize = 14.sp,
                            color = Color.hsl(358f, 0.63f, 0.49f)
                        )
                        CardArrow(expanded.value)
                    }
                },
                modifier = Modifier
                    .clickable {
                        expanded.value = !expanded.value
                    }
                    .drawBehind {
                        val strokeWidth = 1 * density
                        val y = size.height - strokeWidth / 2

                        drawLine(
                            Color.LightGray,
                            Offset(0f, y),
                            Offset(size.width, y),
                            strokeWidth
                        )
                    }
            )

            if (expanded.value) {
                Box(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), contentAlignment = Alignment.TopStart
                ) {
                    Surface(color = Color.hsl(103f, 0f, 0.95f)) {
                        Column(
                            Modifier //TODO: change to LazyColumn
                                .padding(15.dp)
                                .fillMaxWidth()
                        ) {
                            for (expense in item.expenses) {
                                Column(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            expense.getDateStamp(),
                                            style = MaterialTheme.typography.subtitle1
                                        )
                                        Text(
                                            expense.getAmount().toString(),
                                            style = MaterialTheme.typography.subtitle1
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Description: " + expense.getDescription())
                                    }
                                    var imgBase64 =
                                        BackendService(preferences).getExpensePicture(expense.getGlobalId())
                                    if (imgBase64 != null) {
                                        Row(
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {

                                            Image(
                                                bitmap = BitmapFactory.decodeByteArray(
                                                    imgBase64,
                                                    0,
                                                    imgBase64.size
                                                ).asImageBitmap(),
                                                contentDescription = "Image",
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CardArrow(
    bool: Boolean,
) {
    Icon(
        Icons.Default.ArrowDropDown, //TODO: change to different icons
        contentDescription = "Expandable Arrow",
        modifier = Modifier
            .rotate(if (bool) 180f else 0f)
            .size(45.dp)
    )
}
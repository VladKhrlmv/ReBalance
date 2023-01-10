package com.rebalance.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandableList(items: List<ExpenseItem>) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    )  {
        for (item in items) {
            val expanded = remember{ mutableStateOf(false) }
            ListItem(
                text = { Text(item.category) },
                icon = {
                    CardArrow(expanded.value)
                },
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.amount.toString() + " PLN", fontSize = 14.sp, color = Color.hsl(358f, 0.63f, 0.49f))
                    CardArrow(expanded.value)}
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
                        .fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                    Surface(color = Color.hsl(103f, 0f, 0.95f)) {
                        Column(Modifier
                            .padding(16.dp)
                            .fillMaxWidth()) {
                            for (expense in item.expenses) {
                                Row(modifier = Modifier.wrapContentSize().fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(expense.dateStamp.toString(), style = MaterialTheme.typography.subtitle1)
                                    Text(expense.amount.toString(), style = MaterialTheme.typography.subtitle1)
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
        Icons.Default.ArrowDropDown,
        contentDescription = "Expandable Arrow",
        modifier = Modifier.rotate(if (bool) 180f else 0f).size(45.dp)
    )
}
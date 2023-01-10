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


class PersonalCategory(name: String, items: List<CategoryItem>, total: Float) {
    var name = name
    var items= items
    var total = total
}
class CategoryItem(date: String, price: Float) {
    var date = date
    var price = price
}

// items: List<PersonalCategory>

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandableList() {
    val items = listOf<PersonalCategory>(PersonalCategory("first", listOf<CategoryItem>(CategoryItem("1/1/2000", 12.3f), CategoryItem("1/1/2000", 12.3f), CategoryItem("1/1/2000", 12.3f)), 36.9f),
        PersonalCategory("second", listOf<CategoryItem>(CategoryItem("1/1/2000", 12.3f)), 12.3f),
        PersonalCategory("third", listOf<CategoryItem>(CategoryItem("1/1/2000", 12.3f)), 12.3f),
        PersonalCategory("fourth", listOf<CategoryItem>(CategoryItem("1/1/2000", 12.3f)), 12.3f))
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    )  {
        for (category in items) {
            val expanded = remember{ mutableStateOf(false) }
            ListItem(
                text = { Text(category.name) },
                icon = {
                    CardArrow(expanded.value)
                },
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = category.total.toString() + " PLN", fontSize = 14.sp, color = Color.hsl(358f, 0.63f, 0.49f))
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
                            for (item in category.items) {
                                Row(modifier = Modifier.wrapContentSize().fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.date.toString(), style = MaterialTheme.typography.subtitle1)
                                    Text(item.price.toString(), style = MaterialTheme.typography.subtitle1)
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
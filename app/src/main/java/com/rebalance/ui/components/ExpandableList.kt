package com.rebalance.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
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
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.ExpenseItem
import com.rebalance.utils.alertUser
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Trash

@Composable
fun ExpandableList(
    items: List<ExpenseItem>,
    preferences: PreferencesData,
    context: Context
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        for (item in items) {
            val expanded = remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text(item.text) },
                leadingContent = { //TODO: change to category icon
                    CardArrow(expanded.value)
                },
                trailingContent = {
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
                                Row(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        expense.getDateStamp(),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        expense.getAmount().toString(),
                                        style = MaterialTheme.typography.titleMedium
                                    )
//TODO: fix
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .border(1.dp, Color.Black)
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
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                expense.getAmount().toString(),
                                                style = MaterialTheme.typography.titleMedium
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
                                        val imgBase64 =
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
                                        val showDialog = remember { mutableStateOf(false) }

                                        IconButton(onClick = {
                                            showDialog.value = true
                                        }) {
                                            Icon(EvaIcons.Fill.Trash, "Delete expense")
                                        }

                                        if (showDialog.value) {
                                            AlertDialog(
                                                onDismissRequest = { showDialog.value = false },
                                                title = { Text("Confirmation") },
                                                text = { Text("Are you sure you want to delete this expense?") },
                                                confirmButton = {
                                                    TextButton(onClick = {
                                                        BackendService(preferences).deleteExpenseByGlobalId(
                                                            expense.getGlobalId()
                                                        )
                                                        alertUser("Expense deleted!", context)
                                                        showDialog.value = false
                                                        expanded.value = false
                                                        //TODO update screen
                                                    }) {
                                                        Text("Yes")
                                                    }
                                                },
                                                dismissButton = {
                                                    TextButton(onClick = {
                                                        showDialog.value = false
                                                    }) {
                                                        Text("No")
                                                    }
                                                }
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

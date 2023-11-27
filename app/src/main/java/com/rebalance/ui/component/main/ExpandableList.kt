package com.rebalance.ui.component.main

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.ExpenseItem
import com.rebalance.util.alertUser
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Trash

@Composable
fun ExpandableList(
    items: List<ExpenseItem>,
    preferences: PreferencesData,
    context: Context,
    openCategory: MutableState<String>,
    updateData: () -> Unit,
    scrollState: LazyListState
) {
    LazyColumn(state = scrollState) {
        items(items = items, itemContent = { item ->
            val expanded = rememberSaveable { mutableStateOf(item.text == openCategory.value) }
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                ListItem(
                    headlineContent = { Text(item.text) },
                    leadingContent = {
                        //TODO: change to category icon
                    },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format(
                                    if (item.amount >= 100000)
                                        "%,.0f"
                                    else
                                        "%,.2f",
                                    item.amount
                                ) + " PLN",
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
                )

                if (expanded.value) {
                    Column {
                        for ((index, expense) in item.expenses.withIndex()) {
                            Divider()
                            val showPicture = remember { mutableStateOf(false) }
                            ListItem(
                                headlineContent = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        verticalAlignment = Alignment.Bottom
                                    ) {

                                        val text = buildAnnotatedString {

                                            append(
                                                expense.getAmount().toInt().toString()
                                            )

                                            withStyle(
                                                style = SpanStyle(
                                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                    fontStyle = MaterialTheme.typography.bodySmall.fontStyle
                                                )
                                            ) {
                                                append(
                                                    "." + ((expense.getAmount() - expense.getAmount()
                                                        .toInt()) * 100).toInt().toString() + " PLN"
                                                )
                                            }
                                        }

                                        Text(text)
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            expense.getDateStamp(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.padding(bottom = 5.dp, top = 2.dp)
                                        )
                                    }
                                },
                                supportingContent = { Text(expense.getDescription()) },
                                leadingContent = {
                                    DisplayExpenseImage(
                                        preferences,
                                        expense.getGlobalId(),
                                        showPicture,
                                        context
                                    )
                                },
                                trailingContent = {
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
                                                    updateData()
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
                                },
                            )
                        }
                    }
                }
            }

            if (items.indexOf(item) == items.lastIndex) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                )
            }
        })
    }
}


@Composable
fun CardArrow(
    bool: Boolean,
) {
    Icon(
        Icons.Default.ArrowDropDown,
        contentDescription = "Expandable Arrow",
        modifier = Modifier
            .rotate(if (bool) 180f else 0f)
            .size(45.dp)
    )
}

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.backend.dto.ScaledDateItem
import com.rebalance.backend.dto.SumByCategoryItem
import com.rebalance.backend.localdb.entities.Expense
import com.rebalance.backend.service.BackendService
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Trash
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Composable
fun ExpandableList(
    items: List<SumByCategoryItem>,
    context: Context,
    openCategory: MutableState<String>,
    scaledDateItem: ScaledDateItem,
    scrollState: LazyListState,
    deleteItem: (Long) -> Unit
) {
    val backendService = remember { BackendService.get() }
    val personalSpendingListScope = rememberCoroutineScope()

    val expenses = remember { mutableStateMapOf<String, List<Expense>>() }
    val expensesState = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(state = scrollState) {
        items(items = items, itemContent = { item ->
            expensesState[item.category] = item.category == openCategory.value
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
                    headlineContent = { Text(item.category) },
                    leadingContent = {
                        //TODO: change to category icon
                    },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format(
                                    if (item.amount.compareTo(BigDecimal.valueOf(100000)) != -1)
                                        "%,.0f"
                                    else
                                        "%,.2f",
                                    item.amount
                                ) + " ${backendService.getPersonalCurrency()}",
                                fontSize = 14.sp,
                                color = Color.hsl(358f, 0.63f, 0.49f)
                            )
                            expensesState[item.category]?.let { CardArrow(it) }
                        }
                    },
                    modifier = Modifier
                        .clickable {
                            expensesState[item.category] = !expensesState[item.category]!!
                            if (expensesState[item.category]!!) { // if expanded load
                                personalSpendingListScope.launch {
                                    expenses[item.category] =
                                        backendService.getExpensesByCategory(item.category, scaledDateItem)
                                }
                            }
                        }
                )

                if (expensesState[item.category] == true && expenses[item.category]?.isNotEmpty() == true) {
                    Column {
                        for (expense in expenses[item.category]!!) {
                            Divider()
                            var showPicture by remember { mutableStateOf(false) }
                            ListItem(
                                headlineContent = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                        "${expense.amount.setScale(2).toDouble()} " +
                                                backendService.getPersonalCurrency()
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(expense.date),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.padding(bottom = 5.dp, top = 2.dp)
                                        )
                                    }
                                },
                                supportingContent = { Text(expense.description) },
                                leadingContent = {
                                    DisplayExpenseImage(
                                        expense.id,
                                        context,
                                        showPicture,
                                        onIconClick = { showPicture = it }
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
                                                    deleteItem(expense.id)
                                                    showDialog.value = false
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

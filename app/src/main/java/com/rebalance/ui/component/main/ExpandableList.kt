package com.rebalance.ui.component.main

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.ExpenseItem
import com.rebalance.utils.alertUser
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Image
import compose.icons.evaicons.fill.Settings
import compose.icons.evaicons.fill.Trash

@Composable
fun ExpandableList(
    items: List<ExpenseItem>,
    preferences: PreferencesData,
    context: Context
) {
    LazyColumn {
        items(items = items, itemContent = { item ->
            val expanded = remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text(item.text) },
                leadingContent = { //TODO: change to category icon
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
            )

            if (expanded.value) {
//                Box(
//                    Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth(), contentAlignment = Alignment.TopStart
//                ) {
                Column(
                ) {
                    for ((index, expense) in item.expenses.withIndex()) {
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
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            },
                            supportingContent = { Text(expense.getDescription()) },
                            leadingContent = {
                                //TODO: place for image placeholder
                                /*
                                *  Leave this placeholder if there is no image or image is still loading
                                *  After image is loaded change this Icon to the actual image
                                *  On click image should expand to its natural size and displayed to user
                                * */

                                Icon(EvaIcons.Fill.Image, "Image placeholder")

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
                            },
                        )
                        if (index != item.expenses.size - 1) {
                            Divider()
                        }
                    }
                }
            }
        })
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

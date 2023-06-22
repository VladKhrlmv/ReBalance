package com.rebalance.ui.component.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.PreferencesData
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.service.BackendService
import com.rebalance.utils.alertUser
import com.rebalance.utils.displayExpenseImage
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Image
import compose.icons.evaicons.fill.Trash

@Composable
fun GroupSpendingList(
    data: List<Expense>,
    preferences: PreferencesData,
    groupId: Long,
    context: Context,
    refreshAndOpenGroup: (Long) -> Unit,
) {
    val groupCurrency =
        if (groupId == -1L) "" else BackendService(preferences).getGroupById(groupId).getCurrency()


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top
    ) {
        val expensesByGlobalId: Map<Long?, List<Expense>> = data.groupBy { it.getGlobalId() }
        val filteredData = data.filter { it.getAmount() >= 0 }
        items(
            items = filteredData,
            itemContent = { expense ->
                val expanded = rememberSaveable { mutableStateOf(false) }
                val showPicture = remember { mutableStateOf(false) }
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
                            displayExpenseImage(
                                preferences,
                                expense.getGlobalId(),
                                showPicture,
                                context
                            )
                        },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                                refreshAndOpenGroup(groupId)
                                            }) {
                                                Text("Yes")
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showDialog.value = false }) {
                                                Text("No")
                                            }
                                        }
                                    )
                                }

                                CardArrow(expanded.value)
                            }


                        },
                        modifier = Modifier
                            .clickable {
                                expanded.value = !expanded.value
                            }
                    )

                    if (expanded.value) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Category: " + expense.getCategory(),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                            )
                            val payer = expense.getUser()
                            if (payer != null) {
                                Text(
                                    text = "Payed by: " + payer.getUsername(),
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .fillMaxWidth()
                                )
                            }

                            val toWhom = expensesByGlobalId[expense.getGlobalId()]
                            if (toWhom != null && toWhom.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .padding(10.dp)
//                                    .background(Color.White)
                                ) {
                                    Text(
                                        text = "To:",
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .fillMaxWidth()
                                    )
                                    for (expenseWithUser in toWhom.filter { it.getAmount() <= 0 }) {
                                        val user = expenseWithUser.getUser()
                                        if (user != null) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = user.getUsername(),
                                                    fontSize = 14.sp,
                                                    modifier = Modifier
                                                        .padding(horizontal = 10.dp)
                                                )
                                                Text(
                                                    text = "${expenseWithUser.getAmount()} $groupCurrency",
                                                    fontSize = 14.sp,
                                                    modifier = Modifier
                                                        .padding(horizontal = 10.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (filteredData.indexOf(expense) == filteredData.lastIndex) {
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                    )
                }
            }
        )
    }

}

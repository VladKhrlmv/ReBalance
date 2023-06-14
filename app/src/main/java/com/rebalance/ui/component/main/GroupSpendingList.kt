package com.rebalance.ui.component.main

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.PreferencesData
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.service.BackendService
import com.rebalance.utils.alertUser
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Trash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        items(
            items = data.filter { it.getAmount() >= 0 },
            itemContent = { item -> //TODO: apply filter previously
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.getDescription(),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(5.dp)
                            )
                            Text(
                                text = "${item.getAmount()} $groupCurrency",
                                fontSize = 14.sp,
                                color = Color.hsl(358f, 0.63f, 0.49f),
                                modifier = Modifier
                                    .padding(5.dp)
                            )

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
                                                item.getGlobalId()
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
                        }
                        Text(
                            text = "Category: " + item.getCategory(),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                        )
                        Text(
                            text = "Date: " + item.getDateStamp(),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                        )
                        val payer = item.getUser()
                        if (payer != null) {
                            Text(
                                text = "Payed by: " + payer.getUsername(),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                            )
                        }
                        val toWhom = expensesByGlobalId[item.getGlobalId()]
                        if (toWhom != null && toWhom.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .background(Color.White)
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
                        val coroutineScope = rememberCoroutineScope()
                        val imgBase64 =
                            BackendService(preferences).getExpensePicture(item.getGlobalId())
                        val imageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }

                        DisposableEffect(Unit) {
                            if (imgBase64 != null && imageBitmap.value == null) {
                                coroutineScope.launch {
                                    val bitmap = withContext(Dispatchers.IO) {
                                        BitmapFactory.decodeByteArray(
                                            imgBase64,
                                            0,
                                            imgBase64.size
                                        ).asImageBitmap()
                                    }
                                    imageBitmap.value = bitmap
                                }
                            }
                            onDispose { }
                        }

                        if (imageBitmap.value != null) {
                            Image(
                                bitmap = imageBitmap.value!!,
                                contentDescription = "Image",
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        )
    }
}

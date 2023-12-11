package com.rebalance.ui.component.main

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.rebalance.backend.dto.GroupExpenseItem
import com.rebalance.backend.dto.GroupExpenseItemUser
import com.rebalance.backend.localdb.entities.Group
import com.rebalance.backend.service.BackendService
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Trash
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.time.format.DateTimeFormatter

@Composable
fun GroupSpendingList(
    group: Group,
    context: Context,
    onDelete: (Long) -> Unit,
) {
    val backendService = remember { BackendService.get() }
    val groupSpendingListScope = rememberCoroutineScope()

    val expensesLiveData = MutableLiveData<List<GroupExpenseItem>>()
    val expenses by expensesLiveData.observeAsState(initial = emptyList())
    var currentPage by remember { mutableIntStateOf(0) }
    var currentPageLast by remember { mutableStateOf(false) }

    val expenseDebtors = remember { mutableStateMapOf<Long, List<GroupExpenseItemUser>>() }

    fun loadExpenses() {
        groupSpendingListScope.launch {
            val offset = currentPage * 20
            val newExpenses = backendService.getGroupExpenses(group.id, offset)
            if (newExpenses.isNotEmpty()) {
                expensesLiveData.postValue(expensesLiveData.value.orEmpty() + newExpenses)
                currentPage++
            } else {
                currentPageLast = true
            }
        }
    }

    // fetch first elements on start
    LaunchedEffect(Unit) {
        loadExpenses()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top
    ) {
        items(
            items = expenses,
            itemContent = { expense ->
                val expanded = rememberSaveable { mutableStateOf(false) }
                var showPicture by remember { mutableStateOf(false) }
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
                                Text("${expense.amount.setScale(2, RoundingMode.HALF_EVEN)} ${group.currency}")
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
                                            TextButton(onClick = { onDelete(expense.id) }) {
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
                                if (expenseDebtors[expense.id] == null) { // if expanded load debtors
                                    groupSpendingListScope.launch {
                                        expenseDebtors[expense.id] =
                                            backendService.getGroupExpenseDeptors(expense.id)
                                    }
                                }
                            }
                    )

                    if (expanded.value) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Category: " + expense.category,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                            )
                            Text(
                                text =
                                    if (expense.initiator.isEmpty())
                                        ""
                                    else
                                        "Payed by: ${expense.initiator}",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                            )
                            Text(
                                text = "To:",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                            )

                            if (expenseDebtors[expense.id] != null) {
                                for (user in expenseDebtors[expense.id]!!) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = user.user,
                                            fontSize = 14.sp,
                                            modifier = Modifier
                                                .padding(horizontal = 10.dp)
                                        )
                                        Text(
                                            text = "${user.amount.setScale(2, RoundingMode.HALF_EVEN)} ${group.currency}",
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
        )
        item {
            // when at the end, fetch more
            if (expenses.isNotEmpty()) {
                groupSpendingListScope.launch {
                    loadExpenses()
                }
                if (!currentPageLast) {
                    CircularProgressIndicator() // Show loading indicator at the bottom
                }
            }
        }
    }
}

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.rebalance.backend.dto.GroupExpenseItem
import com.rebalance.backend.dto.GroupExpenseItemUser
import com.rebalance.backend.localdb.entities.Group
import com.rebalance.backend.service.BackendService
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Trash
import kotlinx.coroutines.launch

@Composable
fun GroupSpendingList(
    group: Group,
    context: Context,
    onDelete: (Long) -> Unit,
) {
    val backendService = remember { BackendService.get() }
    val groupSpendingListScope = rememberCoroutineScope()

    val _expenses = MutableLiveData<List<GroupExpenseItem>>()
//    val _expenses: LiveData<List<GroupExpenseItem>> = __expenses
    val expenses by _expenses.observeAsState(initial = emptyList())
    var currentPage by rememberSaveable { mutableStateOf(0) }
    var currentPageLast by rememberSaveable { mutableStateOf(false) }

    val expenseDeptors = rememberSaveable { mutableMapOf<Long, List<GroupExpenseItemUser>>() }

    fun loadExpenses() {
        groupSpendingListScope.launch {
            val offset = currentPage * 20
            val newExpenses = backendService.getGroupExpenses(group.id, offset)
            if (newExpenses.isNotEmpty()) {
                _expenses.postValue(_expenses.value.orEmpty() + newExpenses)
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
                var expanded by rememberSaveable { mutableStateOf(false) }
                Card(
                    modifier = Modifier
                        .wrapContentHeight()
                        .clickable {
                            expanded = !expanded;
                            if (expanded) { // if expanded load deptors
                                groupSpendingListScope.launch {
                                    expenseDeptors[expense.id] =
                                        backendService.getGroupExpenseDeptors(expense.id)
                                }
                            }
                        },
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors()
                ) {
                    Column {
                        Text(expense.amount.toString() + group.currency)
                        Text(expense.description)
                        Text(expense.date.toString())
                        IconButton(onClick = {
                            onDelete(expense.id)
                        }) {
                            Icon(EvaIcons.Fill.Trash, "Delete expense")
                        }
                        CardArrow(expanded)
                        if (expenseDeptors[expense.id] != null) {
                            for (user in expenseDeptors[expense.id]!!) {
                                Text(user.user)
                                Text(user.amount.toString())
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
//        val expensesByGlobalId: Map<Long?, List<Expense>> = data.groupBy { it.getGlobalId() }
//        val filteredData = data.filter { it.getAmount() >= 0 }
//        items(
//            items = filteredData,
//            itemContent = { expense ->
//                val expanded = rememberSaveable { mutableStateOf(false) }
//                val showPicture = remember { mutableStateOf(false) }
//                Card(
//                    modifier = Modifier
//                        .padding(10.dp)
//                        .fillMaxWidth()
//                        .wrapContentHeight(),
//                    shape = MaterialTheme.shapes.medium,
//                    elevation = CardDefaults.cardElevation(8.dp),
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//                ) {
//                    ListItem(
//                        headlineContent = {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxSize(),
//                                verticalAlignment = Alignment.Bottom
//                            ) {
//                                val text = buildAnnotatedString {
//                                    append(
//                                        expense.getAmount().toInt().toString()
//                                    )
//                                    withStyle(
//                                        style = SpanStyle(
//                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
//                                            fontStyle = MaterialTheme.typography.bodySmall.fontStyle
//                                        )
//                                    ) {
//                                        append(
//                                            "." + ((expense.getAmount() - expense.getAmount()
//                                                .toInt()) * 100).toInt().toString() + " PLN"
//                                        )
//                                    }
//                                }
//
//                                Text(text)
//                            }
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxSize(),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    expense.getDateStamp(),
//                                    style = MaterialTheme.typography.bodySmall,
//                                    color = MaterialTheme.colorScheme.outline,
//                                    modifier = Modifier.padding(bottom = 5.dp, top = 2.dp)
//                                )
//                            }
//                        },
//                        supportingContent = { Text(expense.getDescription()) },
//                        leadingContent = {
//                            DisplayExpenseImage(
//                                backendService,
//                                expense.getGlobalId(),
//                                showPicture,
//                                context
//                            )
//                        },
//                        trailingContent = {
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                val showDialog = remember { mutableStateOf(false) }
//
//                                IconButton(onClick = {
//                                    showDialog.value = true
//                                }) {
//                                    Icon(EvaIcons.Fill.Trash, "Delete expense")
//                                }
//                                if (showDialog.value) {
//                                    AlertDialog(
//                                        onDismissRequest = { showDialog.value = false },
//                                        title = { Text("Confirmation") },
//                                        text = { Text("Are you sure you want to delete this expense?") },
//                                        confirmButton = {
//                                            TextButton(onClick = {
//                                                backendService.deleteExpenseByGlobalId(
//                                                    expense.getGlobalId()
//                                                )
//                                                alertUser("Expense deleted!", context)
//                                                showDialog.value = false
//                                                refreshAndOpenGroup(groupId)
//                                            }) {
//                                                Text("Yes")
//                                            }
//                                        },
//                                        dismissButton = {
//                                            TextButton(onClick = { showDialog.value = false }) {
//                                                Text("No")
//                                            }
//                                        }
//                                    )
//                                }
//                                CardArrow(expanded.value)
//                            }
//                        },
//                        modifier = Modifier
//                            .clickable {
//                                expanded.value = !expanded.value
//                            }
//                    )
//
//                    if (expanded.value) {
//                        Column(
//                            modifier = Modifier
//                                .padding(horizontal = 10.dp, vertical = 5.dp)
//                        ) {
//                            Text(
//                                text = "Category: " + expense.getCategory(),
//                                fontSize = 14.sp,
//                                modifier = Modifier
//                                    .padding(horizontal = 10.dp)
//                                    .fillMaxWidth()
//                            )
//                            val payer = expense.getUser()
//                            if (payer != null) {
//                                Text(
//                                    text = "Payed by: " + payer.getUsername(),
//                                    fontSize = 14.sp,
//                                    modifier = Modifier
//                                        .padding(horizontal = 10.dp)
//                                        .fillMaxWidth()
//                                )
//                            }
//
//                            val toWhom = expensesByGlobalId[expense.getGlobalId()]
//                            if (toWhom != null && toWhom.isNotEmpty()) {
//                                Column(
//                                    modifier = Modifier
//                                        .padding(10.dp)
//                                ) {
//                                    Text(
//                                        text = "To:",
//                                        fontSize = 14.sp,
//                                        modifier = Modifier
//                                            .padding(horizontal = 10.dp)
//                                            .fillMaxWidth()
//                                    )
//                                    for (expenseWithUser in toWhom.filter { it.getAmount() <= 0 }) {
//                                        val user = expenseWithUser.getUser()
//                                        if (user != null) {
//                                            Row(
//                                                modifier = Modifier.fillMaxWidth(),
//                                                horizontalArrangement = Arrangement.SpaceBetween
//                                            ) {
//                                                Text(
//                                                    text = user.getUsername(),
//                                                    fontSize = 14.sp,
//                                                    modifier = Modifier
//                                                        .padding(horizontal = 10.dp)
//                                                )
//                                                Text(
//                                                    text = "${expenseWithUser.getAmount()} ${group.currency}",
//                                                    fontSize = 14.sp,
//                                                    modifier = Modifier
//                                                        .padding(horizontal = 10.dp)
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                if (filteredData.indexOf(expense) == filteredData.lastIndex) {
//                    Box(
//                        modifier = Modifier
//                            .height(100.dp)
//                    )
//                }
//            }
//        )
    }
}

package com.rebalance.ui.component.main

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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

@SuppressLint("CoroutineCreationDuringComposition")
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
    var isLoading by remember { mutableStateOf(false) }

    val expenseDebtors = remember { mutableStateMapOf<Long, List<GroupExpenseItemUser>>() }

    fun loadExpenses() {
        if (isLoading || currentPageLast) return

        isLoading = true
        groupSpendingListScope.launch {
            val offset = currentPage * 20
            val newExpenses = backendService.getGroupExpenses(group.id, offset)
            isLoading = false
            if (newExpenses.isNotEmpty()) {
                expensesLiveData.postValue(expensesLiveData.value.orEmpty() + newExpenses)
                currentPage++
            } else {
                currentPageLast = true
            }
        }
    }

    // fetch first elements on start
    LaunchedEffect(group) {
        Log.d("group list", "${expenses.size}")
        currentPage = 0
        currentPageLast = false
        expensesLiveData.postValue(listOf())
        loadExpenses()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = expenses,
            itemContent = { expense ->
                val expanded = remember { mutableStateOf(false) }
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
                            Text(
                                "${
                                    expense.amount.setScale(
                                        2,
                                        RoundingMode.HALF_EVEN
                                    )
                                } ${group.currency}"
                            )
                            Text(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                    .format(expense.date),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        },
                        supportingContent = {
                            Text(
                                expense.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        },
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
                                            TextButton(onClick = {
                                                onDelete(expense.id)
                                                showDialog.value = false
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
                                .padding(horizontal = 20.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                                        )
                                    ) {
                                        append("Category: ")
                                    }

                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    ) {
                                        append(expense.category)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.padding(vertical = 4.dp))
                            if (expense.initiator.isNotEmpty()) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                                            )
                                        ) {
                                            append("Payed by: ")
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        ) {
                                            append(expense.initiator)
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.padding(vertical = 4.dp))
                            Divider(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.padding(vertical = 4.dp))

                            if (expenseDebtors[expense.id] == null) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            } else {
                                for (user in expenseDebtors[expense.id]!!) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append(user.user)
                                                }
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = "${
                                                user.amount.setScale(
                                                    2,
                                                    RoundingMode.HALF_EVEN
                                                )
                                            } ${group.currency}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        )
        item {
            // when at the end, fetch more
            if (expenses.isNotEmpty() && !isLoading) {
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

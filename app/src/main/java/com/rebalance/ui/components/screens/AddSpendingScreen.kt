package com.rebalance.ui.components.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.rebalance.Preferences
import com.rebalance.backend.api.sendPost
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.entities.ExpenseGroup
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.components.DatePickerField
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val costValueRegex = """^\d{0,12}[.,]?\d{0,2}${'$'}""".toRegex()

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddSpendingScreen(
    context: Context
) {
    val preferences = rememberSaveable { Preferences(context).read() }

    var spendingName by remember { mutableStateOf(TextFieldValue()) }
    var selectedCategory by remember { mutableStateOf(TextFieldValue()) }
    var costValue by remember { mutableStateOf(TextFieldValue()) }
    var date = remember { mutableStateOf("") }
    var isGroupExpense by remember { mutableStateOf(false) }
    var expandedDropdownGroups by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupId by remember { mutableStateOf(0L) }
    var groupList by remember { mutableStateOf(listOf<ExpenseGroup>()) }
    val membersSelection = remember { mutableStateMapOf<ApplicationUser, Boolean>() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Add spending",
                modifier = Modifier
                    .padding(10.dp),
                fontFamily = FontFamily(Typeface.DEFAULT),
                fontSize = 32.sp,
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                Button(
                    onClick = {
                        spendingName = TextFieldValue("")
                        costValue = TextFieldValue("")
                        selectedCategory = TextFieldValue("")
                        isGroupExpense = false
                    },
                    modifier = Modifier
                        .padding(1.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        Thread {
                            try {
                                if (isGroupExpense) {
                                    val activeMembers =
                                        membersSelection.filterValues { flag -> flag }
                                    if (activeMembers.isEmpty()) {
                                        ContextCompat.getMainExecutor(context).execute {
                                            Toast.makeText(
                                                context,
                                                "Choose at least one member",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    for (member in activeMembers) {
                                        val jsonBodyPOST = sendPost(
                                            "http://${preferences.serverIp}/expenses/user/${member.key.getId()}/group/${groupId}",
                                            Gson().toJson(
                                                Expense(
                                                    costValue.text.toDouble() / activeMembers.size * -1,
                                                    date.value.ifBlank {
                                                        LocalDate.now()
                                                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                    },
                                                    selectedCategory.text,
                                                    spendingName.text
                                                )
                                            )
                                        )
                                        println(jsonBodyPOST)
                                    }
                                    val jsonBodyPOST = sendPost(
                                        "http://${preferences.serverIp}/expenses/user/${preferences.userId}/group/${groupId}",
                                        Gson().toJson(
                                            Expense(
                                                costValue.text.toDouble(),
                                                date.value.ifBlank {
                                                    LocalDate.now()
                                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                },
                                                selectedCategory.text,
                                                spendingName.text
                                            )
                                        )
                                    )
                                    println(jsonBodyPOST)
                                } else {
                                    val jsonBodyPOST = sendPost(
                                        "http://${preferences.serverIp}/expenses/user/${preferences.userId}/group/${
                                            preferences.groupId
                                        }",
                                        Gson().toJson(
                                            Expense(
                                                costValue.text.toDouble(),
                                                date.value.ifBlank {
                                                    LocalDate.now()
                                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                },
                                                selectedCategory.text,
                                                spendingName.text
                                            )
                                        )
                                    )
                                    println(jsonBodyPOST)
                                }
                                ContextCompat.getMainExecutor(context).execute {
                                    Toast.makeText(
                                        context,
                                        "Expense saved!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                print(e.stackTrace)
                                ContextCompat.getMainExecutor(context).execute {
                                    Toast.makeText(
                                        context,
                                        "Unexpected error occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }.start()
                    },
                    modifier = Modifier
                        .padding(1.dp)
                ) {
                    Text("Save")
                }
            }
        }
        TextField(
            value = spendingName,
            onValueChange = { newSpendingName -> spendingName = newSpendingName },
            placeholder = { Text(text = "Title") },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )
        TextField(
            value = selectedCategory,
            onValueChange = { selectedCategory = it },
            label = {
                Text(text = "Category")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        TextField(
            value = costValue,
            onValueChange = { newCostValue ->
                if (costValueRegex.matches(newCostValue.text)) {
                    costValue = newCostValue
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text(text = "0.00") },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .onFocusChanged {
                    if (!it.isFocused) {
                        val tempCostValue = costValue.text
                            .replace(",", ".")
                            .replace("""^\.""".toRegex(), "0.")
                            .replace("""\.$""".toRegex(), ".00")
                        costValue = TextFieldValue(tempCostValue)
                    }
                }
        )
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            DatePickerField(
                date,
                modifier = Modifier
                    .width(180.dp)
            )
            Checkbox(
                checked = isGroupExpense,
                onCheckedChange = {
                    isGroupExpense = it
                    groupList = BackendService(preferences).getGroups()
                        .filter { group -> group.getId() != preferences.groupId }
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "Group expense",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .clickable {
                        isGroupExpense = !isGroupExpense
                        groupList = BackendService(preferences)
                            .getGroups()
                            .filter { group -> group.getId() != preferences.groupId }
                    }
            )
        }
        AnimatedVisibility(
            visible = isGroupExpense,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdownGroups,
                    onExpandedChange = {
                        expandedDropdownGroups = !expandedDropdownGroups
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    TextField(
                        value = groupName,
                        onValueChange = { },
                        readOnly = true,
                        label = {
                            Text(text = "Group")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedDropdownGroups
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdownGroups,
                        onDismissRequest = { expandedDropdownGroups = false },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        groupList.forEach { group ->
                            DropdownMenuItem(
                                onClick = {
                                    groupName = group.getName()
                                    groupId = group.getId()
                                    membersSelection.clear()
                                    group.getUsers().forEach { member ->
                                        membersSelection[member] = false
                                    }
                                    expandedDropdownGroups = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(text = group.getName())
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isGroupExpense,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                items(items = membersSelection.keys.toList(), itemContent = { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        membersSelection[member]?.let {
                            Checkbox(
                                checked = it,
                                onCheckedChange = { newValue ->
                                    membersSelection[member] = newValue
                                },
                            )
                            Text(
                                text = member.getUsername(),
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .clickable {
                                        membersSelection[member] = !membersSelection[member]!!
                                    }
                            )
                        }
                    }

                })
            }
        }
    }
}
package com.rebalance.ui.components.screens

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.ui.components.DatePickerField

val costValueRegex = """^\d{0,12}[.,]?\d{0,2}${'$'}""".toRegex()

var tempGroupMembersDict = mapOf(
    "Group 1" to listOf(
        "Group 1 Member 1",
        "Group 1 Member 2",
        "Group 1 Member 3",
        "Group 1 Member 4"
    ),
    "Group 2" to listOf(
        "Group 2 Member 1",
        "Group 2 Member 2",
        "Group 2 Member 3"
    ),
    "Group 3" to listOf(
        "Group 3 Member 1",
        "Group 3 Member 2",
        "Group 3 Member 3",
        "Group 3 Member 4",
        "Group 3 Member 5"
    )
)

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddSpendingScreen() {
    var spendingName by remember { mutableStateOf(TextFieldValue()) }
    var expandedDropdownCategory by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var costValue by remember { mutableStateOf(TextFieldValue()) }
    var isGroupExpense by remember { mutableStateOf(false) }
    var expandedDropdownGroups by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    val groupMembersDict = tempGroupMembersDict
    val membersSelection = mutableStateMapOf<String, Boolean>()
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
            ){
                Button(
                    onClick = {  },
                    modifier = Modifier
                        .padding(1.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {  },
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
        ExposedDropdownMenuBox(
            expanded = expandedDropdownCategory,
            onExpandedChange = {
                expandedDropdownCategory = !expandedDropdownCategory
            },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value = selectedCategory,
                onValueChange = { },
                readOnly = true,
                label = {
                    Text(text = "Category")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedDropdownCategory
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedDropdownCategory,
                onDismissRequest = { expandedDropdownCategory = false }
            ) {
                DropdownMenuItem(onClick = {
                    selectedCategory = "Sport"; expandedDropdownCategory = false
                }) {
                    // icon can be placed before text
                    Text(text = "Sport")
                }
                DropdownMenuItem(onClick = {
                    selectedCategory = "Clothing"; expandedDropdownCategory = false
                }) {
                    Text(text = "Clothing")
                }
            }
        }
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
                modifier = Modifier
                    .width(180.dp)
            )
            Checkbox(
                checked = isGroupExpense,
                onCheckedChange = {
                    isGroupExpense = it
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
                        groupMembersDict.keys.forEach { group ->
                            DropdownMenuItem(
                                onClick = {
                                    groupName = group
                                    membersSelection.clear()
                                    groupMembersDict[groupName]?.forEach { member ->
                                        membersSelection[member] = false
                                    }
                                    expandedDropdownGroups = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(text = group)
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
                                text = member,
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

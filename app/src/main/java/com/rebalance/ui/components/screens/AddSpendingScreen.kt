package com.rebalance.ui.components.screens

import android.R
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

val costValueRegex = """^\d{0,12}[\.\,]?\d{0,2}${'$'}""".toRegex()

var tempGroupMembersDict = mutableMapOf(
    "Group 1" to mutableMapOf(
        "Group 1 Member 1" to mutableStateOf(false),
        "Group 1 Member 2" to mutableStateOf(false),
        "Group 1 Member 3" to mutableStateOf(false),
        "Group 1 Member 4" to mutableStateOf(false)
    ),
    "Group 2" to mutableMapOf(
        "Group 2 Member 1" to mutableStateOf(false),
        "Group 2 Member 2" to mutableStateOf(false),
        "Group 2 Member 3" to mutableStateOf(false)
    ),
    "Group 3" to mutableMapOf(
        "Group 3 Member 1" to mutableStateOf(false),
        "Group 3 Member 2" to mutableStateOf(false),
        "Group 3 Member 3" to mutableStateOf(false),
        "Group 3 Member 4" to mutableStateOf(false),
        "Group 3 Member 5" to mutableStateOf(false)
    )
)

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
    val groupMembersDict by remember { mutableStateOf(tempGroupMembersDict) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
            .padding(10.dp)
    ) {
        Text(
            text = "Add spending",
            modifier = Modifier
                .align(Alignment.Start)
                .padding(10.dp),
            fontFamily = FontFamily(Typeface.DEFAULT),
            fontSize = 32.sp
        )
        Row {
            TextField(
                value = spendingName,
                onValueChange = { newSpendingName -> spendingName = newSpendingName },
                placeholder = { Text(text = "Title") },
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        Row {
            ExposedDropdownMenuBox(
                expanded = expandedDropdownCategory,
                onExpandedChange = {
                    expandedDropdownCategory = !expandedDropdownCategory
                },
                modifier = Modifier.padding(10.dp)
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
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdownCategory,
                    onDismissRequest = { expandedDropdownCategory = false }
                ) {
                    DropdownMenuItem(onClick = { selectedCategory = "Sport"; expandedDropdownCategory = false }) {
                        // icon can be placed before text
                        Text(text = "Sport")
                    }
                    DropdownMenuItem(onClick = { selectedCategory = "Clothing"; expandedDropdownCategory = false }) {
                        Text(text = "Clothing")
                    }
                }
            }
        }
        Row {
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
                    .onFocusChanged {
                        if (!it.isFocused) {
                            var tempCostValue = costValue.text
                                .replace(",", ".")
                                .replace("""^\.""".toRegex(), "0.")
                                .replace("""\.$""".toRegex(), ".00");
                            costValue = TextFieldValue(tempCostValue)
                        }
                    }
            )

        }
        Row {
            DatePickerField() // TODO do something with date
        }
        Row {
            Checkbox(
                checked = isGroupExpense,
                onCheckedChange = {
                    isGroupExpense = it
                }
            )
            Text(
                text = "Group expense",
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .clickable {
                        isGroupExpense = !isGroupExpense;
                    }
            )
        }
        AnimatedVisibility(visible = isGroupExpense) {
            Row {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdownGroups,
                    onExpandedChange = {
                        expandedDropdownGroups = !expandedDropdownGroups
                    },
                    modifier = Modifier.padding(10.dp)
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
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdownGroups,
                        onDismissRequest = { expandedDropdownGroups = false },
                    ) {
                        groupMembersDict.keys.forEach { group ->
                            DropdownMenuItem(onClick = {
                                groupName = group;
                                expandedDropdownGroups = false
                            }) {
                                Text(text = group)
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = isGroupExpense) {
            LazyColumn(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                var listToPass = if (groupName.isNotBlank()) groupMembersDict[groupName]?.keys?.toList() else listOf()
                items (items = listToPass!!, itemContent = { member ->
                    Row {
                        groupMembersDict[groupName]?.get(member)?.let { checked ->
                            Checkbox(
                                checked = checked.value,
                                onCheckedChange = { groupMembersDict[groupName]?.set(member,
                                    mutableStateOf(!checked.value)
                                ) },
                            )
                            Text(
                                text = member + ' ' + groupMembersDict[groupName]?.get(member)?.value,
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                })
            }
        }
    }
}
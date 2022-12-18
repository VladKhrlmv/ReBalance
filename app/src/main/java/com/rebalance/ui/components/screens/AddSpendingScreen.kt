package com.rebalance.ui.components.screens

import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.ui.components.DatePickerField

val costValueRegex = """^\d{0,12}[\.\,]{0,1}\d{0,2}${'$'}""".toRegex()

var groupMembersDict = mapOf(
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
    var selectedMembers: MutableList<String> = mutableListOf()
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
            Box (contentAlignment = Alignment.Center) {
                IconButton(onClick = { expandedDropdownCategory = true } ) {
                    Row {
                        Icon(
                            imageVector = if (expandedDropdownCategory) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
                            contentDescription = "Open categories",
                            modifier = Modifier
                                .size(64.dp)
                        )
                    }
                }
                DropdownMenu(expanded = expandedDropdownCategory, onDismissRequest = { expandedDropdownCategory = false }) {
                    DropdownMenuItem(onClick = { selectedCategory = "Sport" }) {
                        // icon can be placed before text
                        Text(text = "Sport")
                    }
                    DropdownMenuItem(onClick = { selectedCategory = "Clothing" }) {
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
                                selectedMembers.clear();
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
                var listToPass = if (groupName.isNotBlank()) groupMembersDict[groupName] else listOf()
                items (items = listToPass!!, itemContent = { member ->
                    Row {
                        Checkbox(
                            checked = false,
                            onCheckedChange = {
                                if (it) {
                                    selectedMembers.add(member)
                                }
                                else {
                                    selectedMembers.remove(member)
                                }
                            }
                        )
                        Text(
                            text = member,
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )
                    }
                })
            }
        }
    }
}
package com.rebalance.ui.components.screens

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.DummyGroup
import com.rebalance.backend.service.DummyGroupMember
import com.google.gson.Gson
import com.rebalance.DummyBackend
import com.rebalance.DummyGroup
import com.rebalance.DummyGroupMember
import com.rebalance.backend.GlobalVars
import com.rebalance.backend.api.sendPost
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.DummyGroup
import com.rebalance.backend.service.DummyGroupMember
import com.rebalance.ui.components.DatePickerField
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val costValueRegex = """^\d{0,12}[.,]?\d{0,2}${'$'}""".toRegex()

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddSpendingScreen() {
    var spendingName by remember { mutableStateOf(TextFieldValue()) }
    var selectedCategory by remember { mutableStateOf(TextFieldValue()) }
    var costValue by remember { mutableStateOf(TextFieldValue()) }
    var isGroupExpense by remember { mutableStateOf(false) }
    var expandedDropdownGroups by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupSet by remember { mutableStateOf(mutableSetOf<DummyGroup>()) }
    val membersSelection = remember { mutableStateMapOf<DummyGroupMember, Boolean>() }
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
                                    var jsonBodyPOST = sendPost(
                                        "http://${GlobalVars().getIp()}/expenses/user/${GlobalVars().user.getId()}/group/1",
                                        Gson().toJson(
                                            Expense(
                                                (costValue.text.toFloat() * 100).toInt(),
                                                LocalDate.now()
                                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                                selectedCategory.text,
                                                spendingName.text
                                            )
                                        )
                                    )
                                    println(jsonBodyPOST)
                                } else {
                                    var jsonBodyPOST = sendPost(
                                        "http://${GlobalVars().getIp()}/expenses/user/${GlobalVars().user.getId()}/group/2",
                                        Gson().toJson(
                                            Expense(
                                                (costValue.text.toFloat() * 100).toInt(),
                                                LocalDate.now()
                                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                                selectedCategory.text,
                                                spendingName.text
                                            )
                                        )
                                    )
                                    println(jsonBodyPOST)
                                }
                            } catch (e: Exception) {
                                print(e.stackTrace)
                            }
                        }.start()
                        val currTime: Long = System.currentTimeMillis();
                        while(System.currentTimeMillis() < currTime + 50){
                        }
                        spendingName = TextFieldValue("")
                        costValue = TextFieldValue("")
                        selectedCategory = TextFieldValue("")
                        isGroupExpense = false
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
                modifier = Modifier
                    .width(180.dp)
            )
            Checkbox(
                checked = isGroupExpense,
                onCheckedChange = {
                    isGroupExpense = it
                    groupSet = BackendService().getGroups()
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
                        groupSet = BackendService().getGroups()
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
                        groupSet.forEach { group ->
                            DropdownMenuItem(
                                onClick = {
                                    groupName = group.name
                                    membersSelection.clear()
                                    group.memberList.forEach { member ->
                                        membersSelection[member] = false
                                    }
                                    expandedDropdownGroups = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(text = group.name)
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
                                text = member.name,
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

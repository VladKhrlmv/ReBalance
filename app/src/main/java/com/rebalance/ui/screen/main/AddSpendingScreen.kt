package com.rebalance.ui.screen.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.Preferences
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.ExpenseGroup
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.main.DatePickerField
import com.rebalance.utils.addExpense
import com.rebalance.utils.alertUser

val costValueRegex = """^\d{0,12}[.,]?\d{0,2}${'$'}""".toRegex()

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun AddSpendingScreen(
    context: Context,
    callerPhoto: Bitmap? = null
) {

    val preferences = rememberSaveable { Preferences(context).read() }

    var spendingName by remember { mutableStateOf(TextFieldValue()) }
    var selectedCategory by remember { mutableStateOf(TextFieldValue()) }
    var costValue by remember { mutableStateOf(TextFieldValue()) }
    val date = remember { mutableStateOf("") }
    var isGroupExpense by remember { mutableStateOf(false) }
    var expandedDropdownGroups by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupId by remember { mutableStateOf(0L) }
    var groupIdLast by remember { mutableStateOf(0L) }
    var groupList by remember { mutableStateOf(listOf<ExpenseGroup>()) }
    val membersSelection = remember { mutableStateMapOf<ApplicationUser, Boolean>() }

    var selectedPhoto by remember { mutableStateOf(callerPhoto) }
    var photoName by remember { mutableStateOf("") }

    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val bitmap = context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                }

                val fileNameColumn = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
                val cursor = context.contentResolver.query(uri, fileNameColumn, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(fileNameColumn[0])
                    photoName = cursor.getString(columnIndex)
                    cursor.close()
                }

                if (bitmap != null) {
                    selectedPhoto = bitmap
                }
            }
        }
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
                fontSize = 28.sp,
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                Button(
                    onClick = {
                        if (spendingName.text.isEmpty() || costValue.text.isEmpty() || selectedCategory.text.isEmpty()) {
                            alertUser("Fill in all data", context)
                            return@Button
                        }
                        Thread {
                            try {
                                addExpense(
                                    isGroupExpense,
                                    membersSelection,
                                    context,
                                    preferences,
                                    groupId,
                                    costValue,
                                    date,
                                    selectedCategory,
                                    spendingName,
                                    selectedPhoto
                                )
                                spendingName = TextFieldValue("")
                                costValue = TextFieldValue("")
                                selectedCategory = TextFieldValue("")
                                date.value = ""
                                isGroupExpense = false
                                groupName = ""
                                groupId = 0L
                                membersSelection.clear()
                                alertUser("Expense saved!", context)
                            } catch (e: Exception) {
                                print(e.stackTrace)
                                alertUser("Unexpected error occurred:\n" + e.message, context)
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
                .testTag("addCost"),
            trailingIcon = {
                Text(
                    text = BackendService(preferences).getGroupById(if (groupId == 0L) preferences.groupId else groupId)
                        .getCurrency()
                )
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
                    if (isGroupExpense) {
                        groupId = groupIdLast
                    } else {
                        groupIdLast = groupId
                        groupId = 0L
                    }
                    groupList = BackendService(preferences).getGroups()
                        .filter { group -> group.getId() != preferences.groupId }
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .testTag("groupExpenseCheckBox")
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
                        if (isGroupExpense) {
                            groupId = groupIdLast
                        } else {
                            groupIdLast = groupId
                            groupId = 0L
                        }
                    }
            )
        }
        Button(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text("Choose photo from gallery")
        }
        if (selectedPhoto != null) {
            Text(
                text = "Selected photo: $photoName (${selectedPhoto!!.width}x${selectedPhoto!!.height})",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
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
                        .testTag("groupSelectExpenseDropdown")
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
                                text = {
                                    Text(text = group.getName())
                                },
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
                            )
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

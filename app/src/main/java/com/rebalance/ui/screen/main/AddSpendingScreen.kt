package com.rebalance.ui.screen.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.entities.ExpenseGroup
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.main.DatePickerField
import com.rebalance.ui.component.main.GroupSelection
import com.rebalance.ui.navigation.navigateUp
import com.rebalance.utils.*
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("UnrememberedMutableState")
@Composable
fun AddSpendingScreen(
    context: Context,
    navHostController: NavHostController,
    callerPhoto: Bitmap? = null
) {

    val preferences = rememberSaveable { Preferences(context).read() }

    var spendingName by remember { mutableStateOf(TextFieldValue()) }
    var selectedCategory by remember { mutableStateOf(TextFieldValue()) }
    var costValue by remember { mutableStateOf(TextFieldValue()) }
    val date = remember { mutableStateOf("") }
    var isGroupExpense by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupId by remember { mutableStateOf(0L) }
    var groupIdLast by remember { mutableStateOf(0L) }
    var groupList by remember { mutableStateOf(listOf<ExpenseGroup>()) }
    val membersSelection = remember { mutableStateMapOf<ApplicationUser, Boolean>() }

    var selectedPhoto by remember { mutableStateOf(callerPhoto) }
    var photoName by remember { mutableStateOf("") }

    val focusRequesters = remember { List(3) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

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

    // scroll state of column
    val scrollState = rememberScrollState()

    // outer column
    Column(
        modifier = Modifier
    ) {
        // title and button Save
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
                        if (isGroupExpense && membersSelection.filterValues { flag -> flag }
                                .isEmpty()) {
                            alertUser("Choose at least one member", context)
                            return@Button
                        }

                        try {
                            addExpense(
                                isGroupExpense,
                                membersSelection,
                                preferences,
                                groupId,
                                costValue,
                                date,
                                selectedCategory,
                                spendingName,
                                compressImage(selectedPhoto)
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
                            navigateUp(navHostController)
                        } catch (e: Exception) {
                            alertUser("Unexpected error occurred:\n" + e.message, context)
                        }
                    },
                    modifier = Modifier
                        .padding(1.dp)
                ) {
                    Text("Save")
                }
            }
        }

        // scrollable column with other content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(10.dp)
        ) {
            // Title field
            TextField(
                value = spendingName,
                onValueChange = { newSpendingName -> spendingName = newSpendingName },
                label = {
                    Text(text = "Title")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequesters[0]),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
            // Category field
            TextField(
                value = selectedCategory,
                onValueChange = { selectedCategory = it },
                label = {
                    Text(text = "Category")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .focusRequester(focusRequesters[1]),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
            // Cost field
            TextField(
                value = costValue,
                onValueChange = { newCostValue ->
                    if (costValueRegex().matches(newCostValue.text)) {
                        costValue = newCostValue
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                label = {
                    Text(text = "Cost")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequesters[2])
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
            // Date picker and Group checkbox fields
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
            // Button to upload picture
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
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
            // animated Group selector
            AnimatedVisibility(
                visible = isGroupExpense,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // checkboxes for all selected group's members
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("groupSelectExpenseDropdown")
                ) {
                    GroupSelection(
                        preferences,
                        groupName,
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        Modifier
                            .fillMaxWidth(),
                        onSwitch = {
                            groupId = it
                            val group = BackendService(preferences).getGroupById(groupId)
                            groupName = group.getName()
                            membersSelection.clear()
                            group.getUsers().forEach { member ->
                                membersSelection[member] = false
                            }
                        }
                    )

                    membersSelection.keys.toList().forEach { member ->
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
                    }
                }
            }
        }
    }
}

fun getToday(): String {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun compressImage(originalImage: Bitmap?): ByteArrayOutputStream? {
    if (originalImage == null) {
        return null
    }
    val outputStream = ByteArrayOutputStream()
    originalImage.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
    return outputStream
}

fun addExpense(
    isGroupExpense: Boolean,
    membersSelection: SnapshotStateMap<ApplicationUser, Boolean>,
    preferences: PreferencesData,
    groupId: Long,
    costValue: TextFieldValue,
    date: MutableState<String>,
    selectedCategory: TextFieldValue,
    spendingName: TextFieldValue,
    callerPhoto: ByteArrayOutputStream?
) {
    if (isGroupExpense) {
        val activeMembers =
            membersSelection.filterValues { flag -> flag }
        val resultExpense = BackendService(preferences).addExpense(
            Expense(
                costValue.text.toDouble(),
                date.value.ifBlank { getToday() },
                selectedCategory.text,
                spendingName.text
            ),
            groupId
        )
        for (member in activeMembers) {
            BackendService(preferences).addExpense(
                Expense(
                    costValue.text.toDouble() / activeMembers.size * -1,
                    date.value.ifBlank { getToday() },
                    selectedCategory.text,
                    spendingName.text,
                    resultExpense.getGlobalId()
                ),
                groupId,
                member.key.getId()
            )
            if (callerPhoto != null) {
                val b = callerPhoto.toByteArray()
                val base64String: String = Base64.encodeToString(
                    b,
                    Base64.DEFAULT
                )

                BackendService(preferences).addExpenseImage(
                    base64String,
                    resultExpense.getGlobalId()
                )
            }
        }
    } else {
        val resultExpense = BackendService(preferences).addExpense(
            Expense(
                costValue.text.toDouble(),
                date.value.ifBlank { getToday() },
                selectedCategory.text,
                spendingName.text
            ),
            preferences.groupId
        )
        if (callerPhoto != null) {
            val b = callerPhoto.toByteArray()
            val base64String: String = Base64.encodeToString(
                b,
                Base64.DEFAULT
            )
            BackendService(preferences).addExpenseImage(base64String, resultExpense.getGlobalId())
        }
    }
}

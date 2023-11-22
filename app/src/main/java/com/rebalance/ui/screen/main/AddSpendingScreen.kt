package com.rebalance.ui.screen.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.backend.api.entities.ApplicationUser
import com.rebalance.backend.api.entities.Expense
import com.rebalance.backend.api.entities.ExpenseGroup
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.component.main.DatePickerField
import com.rebalance.ui.component.main.GroupMemberSelection
import com.rebalance.ui.component.main.GroupSelection
import com.rebalance.ui.navigation.navigateUp
import com.rebalance.util.*
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Image
import compose.icons.evaicons.fill.Trash
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun AddSpendingScreen(
    context: Context,
    navHostController: NavHostController,
    callerPhoto: Bitmap? = null,
    setOnPlusClick: (() -> Unit) -> Unit
) {
    val backendService = BackendService(context)
    var spendingName by remember { mutableStateOf(TextFieldValue()) }
    var selectedCategory by remember { mutableStateOf(TextFieldValue()) }
    var costValue by remember { mutableStateOf(TextFieldValue()) }
    val date = remember { mutableStateOf("") }
    var isGroupExpense by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupId by remember { mutableStateOf(0L) }
    var groupIdLast by remember { mutableStateOf(0L) }
    var groupList by remember { mutableStateOf(listOf<ExpenseGroup>()) }
    val payer = mutableStateOf(ApplicationUser())
    val membersSelection = remember { mutableStateMapOf<ApplicationUser, Pair<Boolean, Int>>() }

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
    LaunchedEffect(Unit) {
        setOnPlusClick {
            if (spendingName.text.isEmpty() || costValue.text.isEmpty() || selectedCategory.text.isEmpty()) {
                alertUser("Fill in all data", context)
                return@setOnPlusClick
            }
            if (isGroupExpense && membersSelection.filterValues { flag -> flag.first }
                    .isEmpty()) {
                alertUser("Choose at least one member", context)
                return@setOnPlusClick
            }

            try {
                addExpense(
                    isGroupExpense,
                    membersSelection,
                    backendService,
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
        }
    }
    // scroll state of column
    val scrollState = rememberScrollState()

    // outer column
    Column(
        modifier = Modifier
    ) {
        // scrollable column with other content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 10.dp, end = 10.dp)
        ) {
            // Title and image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Title field
                TextField(
                    value = spendingName,
                    onValueChange = { newSpendingName -> spendingName = newSpendingName },
                    label = {
                        Text(text = "Title")
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .focusRequester(focusRequesters[0])
                        .align(Alignment.CenterStart)
                        .width(275.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    maxLines = 1
                )
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterEnd)
                        .size(64.dp)
                ) {
                    if (selectedPhoto == null) {
                        Icon(
                            EvaIcons.Fill.Image,
                            "Image",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        Icon(
                            Bitmap.createScaledBitmap(
                                selectedPhoto!!,
                                175,
                                175,
                                false
                            ).asImageBitmap(),
                            "Image",
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            // If picture is chosen
            if (selectedPhoto != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                ) {
                    Text(
                        photoName,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .widthIn(max = 270.dp)
                            .padding(horizontal = 12.dp)
                            .align(Alignment.CenterVertically)
                    )
                    IconButton(onClick = {
                        photoName = ""
                        selectedPhoto = null
                    }) {
                        Icon(
                            EvaIcons.Fill.Trash,
                            "Delete attachment",
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
            // Category field
            TextField(
                value = selectedCategory,
                onValueChange = { selectedCategory = it },
                label = {
                    Text(text = "Category")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .focusRequester(focusRequesters[1]),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                maxLines = 1
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
                    .padding(horizontal = 10.dp, vertical = 5.dp)
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
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                ),
                maxLines = 1,
                trailingIcon = {
                    Text(
                        text = backendService.getGroupById(if (groupId == 0L) backendService.getGroupId() else groupId)
                            .getCurrency()
                    )
                }
            )
            // Date picker and Group checkbox fields
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
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
                        groupList = backendService.getGroups()
                            .filter { group -> group.getId() != backendService.getGroupId() }
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
                            groupList = backendService
                                .getGroups()
                                .filter { group -> group.getId() != backendService.getGroupId() }
                            if (isGroupExpense) {
                                groupId = groupIdLast
                            } else {
                                groupIdLast = groupId
                                groupId = 0L
                            }
                        }
                )
            }
            // animated Group selector
            AnimatedVisibility(
                visible = isGroupExpense,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                // checkboxes for all selected group's members
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("groupSelectExpenseDropdown")
                ) {
                    // Group selection
                    GroupSelection(
                        backendService,
                        groupName,
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        Modifier
                            .fillMaxWidth(),
                        onSwitch = {
                            groupId = it
                            val group = backendService.getGroupById(groupId)
                            groupName = group.getName()
                            membersSelection.clear()
                            group.getUsers().forEach { member ->
                                membersSelection[member] = Pair(false, 1)
                            }
                            payer.value = ApplicationUser()
                        }
                    )
                    // Payer field
                    GroupMemberSelection(
                        backendService = backendService,
                        memberSet =
                        if (groupId != 0L)
                            backendService.getGroupById(groupId).getUsers()
                        else
                            setOf(),
                        memberName = payer.value.getUsername(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        innerModifier = Modifier
                            .fillMaxWidth(),
                        onSwitch = {
                            payer.value = it
                        }
                    )
                    Column(
                        modifier = Modifier
                            .heightIn(100.dp, 175.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 65.dp)
                    )
                    {
                        membersSelection.keys.toList().forEach { member ->
                            membersSelection[member]?.let {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .width(275.dp)
                                    ) {
                                        Checkbox(
                                            checked = it.first,
                                            onCheckedChange = { newValue ->
                                                membersSelection[member] =
                                                    Pair(newValue, it.second)
                                            },
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                        )
                                        Text(
                                            text = member.getUsername(),
                                            modifier = Modifier
                                                .width(200.dp)
                                                .clickable {
                                                    membersSelection[member] =
                                                        Pair(
                                                            !membersSelection[member]!!.first,
                                                            membersSelection[member]!!.second
                                                        )
                                                }
                                                .align(Alignment.CenterVertically),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                    ) {
                                        if (membersSelection[member]!!.first) {
                                            TextField(
                                                value = if (membersSelection[member]!!.second != 0)
                                                    membersSelection[member]!!.second.toString()
                                                else
                                                    "",
                                                onValueChange = { newValue: String ->
                                                    if (positiveIntegerRegex().matches(newValue)) {
                                                        membersSelection[member] = Pair(
                                                            membersSelection[member]!!.first,
                                                            newValue.toInt()
                                                        )
                                                    } else if (newValue == "") {
                                                        membersSelection[member] = Pair(
                                                            membersSelection[member]!!.first,
                                                            0
                                                        )
                                                    }
                                                },
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Done
                                                ),
                                                modifier = Modifier
                                                    .width(50.dp)
                                                    .align(Alignment.CenterVertically),
                                                colors = TextFieldDefaults.textFieldColors(
                                                    containerColor = Color.Transparent,
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
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
    membersSelection: SnapshotStateMap<ApplicationUser, Pair<Boolean, Int>>,
    backendService: BackendService,
    groupId: Long,
    costValue: TextFieldValue,
    date: MutableState<String>,
    selectedCategory: TextFieldValue,
    spendingName: TextFieldValue,
    callerPhoto: ByteArrayOutputStream?
) {
    if (isGroupExpense) {
        val activeMembers =
            membersSelection.filterValues { flag -> flag.first }
        val resultExpense = backendService.addExpense(
            Expense(
                costValue.text.toDouble(),
                date.value.ifBlank { getToday() },
                selectedCategory.text,
                spendingName.text
            ),
            groupId
        )
        for (member in activeMembers) {
            backendService.addExpense(
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

                backendService.addExpenseImage(
                    base64String,
                    resultExpense.getGlobalId()
                )
            }
        }
    } else {
        val resultExpense = backendService.addExpense(
            Expense(
                costValue.text.toDouble(),
                date.value.ifBlank { getToday() },
                selectedCategory.text,
                spendingName.text
            ),
            backendService.getGroupId()
        )
        if (callerPhoto != null) {
            val b = callerPhoto.toByteArray()
            val base64String: String = Base64.encodeToString(
                b,
                Base64.DEFAULT
            )
            backendService.addExpenseImage(base64String, resultExpense.getGlobalId())
        }
    }
}

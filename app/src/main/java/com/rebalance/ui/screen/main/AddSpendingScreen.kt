package com.rebalance.ui.screen.main

import android.content.Context
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.backend.dto.NewGroupSpending
import com.rebalance.backend.dto.NewPersonalSpending
import com.rebalance.backend.dto.SpendingDeptor
import com.rebalance.backend.localdb.entities.Group
import com.rebalance.backend.localdb.entities.User
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
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpendingScreen(
    context: Context,
    navHostController: NavHostController,
    callerPhoto: ImageBitmap? = null,
    setOnPlusClick: (() -> Unit) -> Unit
) {
    val backendService = remember { BackendService.get() }
    val addSpendingScreenScope = rememberCoroutineScope()

    var spendingName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var costValue by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(Date()) }
    var isGroupExpense by remember { mutableStateOf(false) }

    var personalGroup: Group? by remember { mutableStateOf(null) }
    var groupList by remember { mutableStateOf(listOf<Group>()) }
    var groupIndex by remember { mutableStateOf(0) }

    var payer: User? by remember { mutableStateOf(null) }
    var payerId by remember { mutableStateOf(0L) }
    var membersSelection by remember { mutableStateOf(listOf<SpendingDeptor>()) }

    var selectedPhoto by remember { mutableStateOf(callerPhoto) }
    var photoName by remember { mutableStateOf("") }

    //TODO: add focusers to each field
//    val focusRequesters = remember { List(3) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    // selecting image
    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                Log.d("add", "start getting image")
                val bitmap = context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                }
                Log.d("add", "image ${bitmap?.height}x${bitmap?.width}")

                val fileNameColumn = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
                Log.d("add", "image name $fileNameColumn")
                val cursor = context.contentResolver.query(uri, fileNameColumn, null, null, null)
                Log.d("add", "open cursor")
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(fileNameColumn[0])
                    photoName = cursor.getString(columnIndex)
                    cursor.close()
                    Log.d("add", "photo name $photoName")
                }

                if (bitmap != null) {
                    selectedPhoto = bitmap.asImageBitmap()
                    Log.d("add", "select photo")
                }
            }
        }

    // on start fetch personal group and group list
    LaunchedEffect(Unit) {
        personalGroup = backendService.getGroupById(backendService.getGroupId())
        groupList = backendService.getUserGroups()
    }

    // set action for floating button
    LaunchedEffect(Unit) {
        setOnPlusClick {
            if (spendingName.isEmpty() || costValue.isEmpty() || selectedCategory.isEmpty()) {
                alertUser("Fill in all data", context)
                return@setOnPlusClick
            }
            if (isGroupExpense && membersSelection.all { deptor -> !deptor.selected }) {
                alertUser("Choose at least one member", context)
                return@setOnPlusClick
            }
            if (isGroupExpense && payer == null) {
                alertUser("Set the payer", context)
                return@setOnPlusClick
            }

            if (isGroupExpense) {
                val newGroupSpending = NewGroupSpending(
                    backendService.getUserId(),
                    groupList[groupIndex].id,
                    costValue.toBigDecimal(),
                    spendingName,
                    selectedCategory,
                    date,
                    membersSelection.filter { it.selected }
                )
                addSpendingScreenScope.launch {
                    backendService.addNewGroupExpense(newGroupSpending, selectedPhoto, photoName)
                }
            } else {
                val newPersonalSpending = NewPersonalSpending(
                    costValue.toBigDecimal(),
                    spendingName,
                    selectedCategory,
                    date
                )
                addSpendingScreenScope.launch {
                    backendService.addNewPersonalExpense(
                        newPersonalSpending,
                        selectedPhoto,
                        photoName
                    )
                }
            }
            navigateUp(navHostController)
        }
    }

    // fetch users of selected group
    LaunchedEffect(groupIndex, groupList) {
        if (groupList.size > groupIndex) {
            membersSelection = backendService.getUsersOfGroup(groupList[groupIndex].id)
        }
    }

    // fetch payer on change
    LaunchedEffect(payerId) {
        payer = backendService.getUserById(payerId)
    }

    fun updateSelection(userId: Long, selection: Boolean) {
        membersSelection =
            membersSelection.map { if (it.userId == userId) it.copyWithSelected(selection) else it }
    }

    fun updateMultiplier(userId: Long, multiplier: Int) {
        membersSelection =
            membersSelection.map { if (it.userId == userId) it.copyWithMultiplier(multiplier) else it }
    }

    // outer column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { /* Do nothing on press to avoid ripple effect */
                    },
                    onTap = { focusManager.clearFocus() }
                )
            }
    ) {
        // scrollable column with other content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                    singleLine = true
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
//                            Bitmap.createScaledBitmap(
//                                selectedPhoto!!,
//                                175,
//                                175,
//                                false
//                            ).asImageBitmap(),
                            selectedPhoto!!, //TODO: scale image to fit icon
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
                    .padding(horizontal = 10.dp, vertical = 5.dp),
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
                singleLine = true
            )
            // Cost field
            TextField(
                value = costValue,
                onValueChange = { newCostValue ->
                    if (costValueRegex().matches(newCostValue)) {
                        costValue = newCostValue.replace(",", ".")
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
                    .onFocusChanged {
                        if (!it.isFocused) {
                            costValue = costValue
                                .replace(",", ".")
                                .replace("""^\.""".toRegex(), "0.")
                                .replace("""\.$""".toRegex(), ".00")
                        }
                    }
                    .testTag("addCost"),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                ),
                singleLine = true,
                trailingIcon = {
                    Text(
                        // if group expense and list of groups is loaded (or not empty), get currency from there
                        text = if (isGroupExpense && groupList.size > groupIndex) {
                            groupList[groupIndex].currency
                        } else { //otherwise get personal currency (if loaded, or empty)
                            personalGroup?.currency ?: ""
                        }
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
                        .width(180.dp),
                    onChange = {
                        date = it
                    }
                )
                //TODO: wrap checkbox and text into row and make it clickable to select
                Checkbox(
                    checked = isGroupExpense,
                    onCheckedChange = {
                        isGroupExpense = it
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
                        if (groupList.size > groupIndex) groupList[groupIndex] else null,
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        Modifier
                            .fillMaxWidth(),
                        onSwitch = { newGroupId ->
                            // find group with selected id
                            for ((index, group) in groupList.withIndex()) {
                                if (group.id == newGroupId) {
                                    groupIndex = index
                                    break
                                }
                            }
                        }
                    )
                    // Payer field
                    GroupMemberSelection(
                        members = membersSelection,
                        payer = payer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        innerModifier = Modifier
                            .fillMaxWidth(),
                        onSwitch = {
                            payerId = it
                        }
                    )
                    // deptors choosing
                    Column(
                        modifier = Modifier
                            .heightIn(100.dp, 175.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 65.dp)
                    )
                    {
                        for (member in membersSelection) {
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
                                        checked = member.selected,
                                        onCheckedChange = { newValue ->
                                            updateSelection(member.userId, newValue)
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                    )
                                    Text(
                                        text = member.nickname,
                                        modifier = Modifier
                                            .width(200.dp)
                                            .clickable {
                                                updateSelection(member.userId, !member.selected)
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
                                    if (member.selected) {
                                        TextField(
                                            value = if (member.multiplier != 0)
                                                member.multiplier.toString()
                                            else
                                                "",
                                            onValueChange = { newValue: String ->
                                                if (positiveIntegerRegex().matches(newValue)) {
                                                    updateMultiplier(
                                                        member.userId,
                                                        newValue.toInt()
                                                    )
                                                    Log.d(
                                                        "add",
                                                        "mult1 $newValue ${member.multiplier}"
                                                    )
                                                } else if (newValue == "") {
                                                    updateMultiplier(member.userId, 0)
                                                    Log.d("add", "mult0 ${member.multiplier}")
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

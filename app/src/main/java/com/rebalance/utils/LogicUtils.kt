package com.rebalance.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.text.input.TextFieldValue
import com.rebalance.PreferencesData
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.entities.ExpenseGroup
import com.rebalance.backend.service.BackendService
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getToday(): String {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun addExpense(
    isGroupExpense: Boolean,
    membersSelection: SnapshotStateMap<ApplicationUser, Boolean>,
    context: Context,
    preferences: PreferencesData,
    groupId: Long,
    costValue: TextFieldValue,
    date: MutableState<String>,
    selectedCategory: TextFieldValue,
    spendingName: TextFieldValue,
    callerPhoto: Bitmap?
) {
    if (isGroupExpense) {
        val activeMembers =
            membersSelection.filterValues { flag -> flag }
        if (activeMembers.isEmpty()) {
            alertUser("Choose at least one member", context)
            return
        }
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
                groupId
            )
            if (callerPhoto != null) {
                val baos = ByteArrayOutputStream()
                callerPhoto.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    baos
                )
                val b = baos.toByteArray()
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
            val baos = ByteArrayOutputStream()
            callerPhoto.compress(
                Bitmap.CompressFormat.PNG,
                100,
                baos
            )
            val b = baos.toByteArray()
            val base64String: String = Base64.encodeToString(
                b,
                Base64.DEFAULT
            )
            BackendService(preferences).addExpenseImage(base64String, resultExpense.getGlobalId())
        }
    }
}

fun createGroup(
    groupCurrency: TextFieldValue,
    groupName: TextFieldValue,
    context: Context,
    preferences: PreferencesData
): ExpenseGroup? {
    if (groupCurrency.text.length != 3 || groupName.text.isBlank()) {
        alertUser("Fill in all fields!", context)
        return null
    }
    val group = BackendService(preferences).createGroup(groupCurrency.text, groupName.text)
    alertUser("Group was created!", context)
    return group
}

fun compressImage(originalImage: Bitmap?, context: Context): Bitmap? {
    if (originalImage == null) {
        return null
    }
    val outputStream = ByteArrayOutputStream()
    originalImage.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
    val filePath = context.cacheDir.absolutePath + "/compressed.jpg"
    val file = File(filePath)
    try {
        val fos = FileOutputStream(file)
        fos.write(outputStream.toByteArray())
        fos.flush()
        fos.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return BitmapFactory.decodeFile(filePath)
}

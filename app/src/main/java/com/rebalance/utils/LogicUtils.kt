package com.rebalance.utils

import android.content.Context
import android.os.StrictMode
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.text.input.TextFieldValue
import com.google.gson.Gson
import com.rebalance.PreferencesData
import com.rebalance.backend.api.RequestsSender
import com.rebalance.backend.api.jsonToExpense
import com.rebalance.backend.api.jsonToExpenseGroup
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.entities.ExpenseGroup
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
    spendingName: TextFieldValue
) {
    if (isGroupExpense) {
        val activeMembers =
            membersSelection.filterValues { flag -> flag }
        if (activeMembers.isEmpty()) {
            alertUser("Choose at least one member", context)
            return
        }
        val jsonBodyPOST = RequestsSender.sendPost(
            "http://${preferences.serverIp}/expenses/user/${preferences.userId}/group/${groupId}/${preferences.userId}",
            Gson().toJson(
                Expense(
                    costValue.text.toDouble(),
                    date.value.ifBlank { getToday() },
                    selectedCategory.text,
                    spendingName.text
                )
            )
        )
        val resultExpense = jsonToExpense(jsonBodyPOST)
        println(jsonBodyPOST)
        for (member in activeMembers) {
            val jsonBodyPOST = RequestsSender.sendPost(
                "http://${preferences.serverIp}/expenses/user/${member.key.getId()}/group/${groupId}/${preferences.userId}",
                Gson().toJson(
                    Expense(
                        costValue.text.toDouble() / activeMembers.size * -1,
                        date.value.ifBlank { getToday() },
                        selectedCategory.text,
                        spendingName.text,
                        resultExpense.getGlobalId()
                    )
                )
            )
            println(jsonBodyPOST)
        }
    } else {
        val jsonBodyPOST = RequestsSender.sendPost(
            "http://${preferences.serverIp}/expenses/user/${preferences.userId}/group/${
                preferences.groupId
            }/${preferences.userId}",
            Gson().toJson(
                Expense(
                    costValue.text.toDouble(),
                    date.value.ifBlank { getToday() },
                    selectedCategory.text,
                    spendingName.text
                //TODO IF globalId != -1 notifications are not working
                )
            )
        )
        println(jsonBodyPOST)
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
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    val group = jsonToExpenseGroup(
        RequestsSender.sendPost(
            "http://${preferences.serverIp}/users/${preferences.userId}/groups",
            "{\"currency\": \"${groupCurrency.text}\", \"name\": \"${groupName.text}\"}"
        )
    )
    alertUser("Group was created!", context)
    return group
}
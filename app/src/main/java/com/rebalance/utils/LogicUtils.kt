package com.rebalance.utils

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.text.input.TextFieldValue
import com.google.gson.Gson
import com.rebalance.PreferencesData
import com.rebalance.backend.api.RequestsSender
import com.rebalance.backend.api.jsonToExpense
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.Expense
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getToday(): String {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun addExpense(isGroupExpense: Boolean,
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
                    spendingName.text,
                    -1L
                )
            )
        )
        println(jsonBodyPOST)
    }
}
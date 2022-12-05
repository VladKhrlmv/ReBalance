package com.rebalance.backend.api

import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.entities.ExpenseGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

public fun jsonArrayToApplicationUsers(jsonBody: String): List<ApplicationUser> {
    val typeToken = object : TypeToken<List<ApplicationUser>>() {}.type
    return Gson().fromJson(jsonBody, typeToken)
}

public fun jsonArrayToExpenses(jsonBody: String): List<Expense> {
    val typeToken = object : TypeToken<List<Expense>>() {}.type
    return Gson().fromJson(jsonBody, typeToken)
}

public fun jsonArrayToExpenseGroups(jsonBody: String): List<ExpenseGroup> {
    val typeToken = object : TypeToken<List<ExpenseGroup>>() {}.type
    return Gson().fromJson(jsonBody, typeToken)
}

public fun jsonToApplicationUser(jsonBody: String): ApplicationUser {
    return Gson().fromJson(jsonBody, ApplicationUser::class.java)
}

public fun jsonToExpense(jsonBody: String): Expense {
    return Gson().fromJson(jsonBody, Expense::class.java)
}

public fun jsonToExpenseGroup(jsonBody: String): ExpenseGroup {
    return Gson().fromJson(jsonBody, ExpenseGroup::class.java)
}
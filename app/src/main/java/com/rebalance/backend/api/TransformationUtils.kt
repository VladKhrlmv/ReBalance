package com.rebalance.backend.api

import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.entities.ExpenseGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rebalance.backend.entities.LoginAndPassword

fun jsonArrayToApplicationUsers(jsonBody: String): List<ApplicationUser> {
    val typeToken = object : TypeToken<List<ApplicationUser>>() {}.type
    return Gson().fromJson(jsonBody, typeToken)
}

fun jsonArrayToExpenses(jsonBody: String): ArrayList<Expense> {
    val typeToken = object : TypeToken<ArrayList<Expense>>() {}.type
    return Gson().fromJson(jsonBody, typeToken)
}

fun jsonArrayToExpenseGroups(jsonBody: String): List<ExpenseGroup> {
    val typeToken = object : TypeToken<List<ExpenseGroup>>() {}.type
    return Gson().fromJson(jsonBody, typeToken)
}

fun jsonToApplicationUser(jsonBody: String): ApplicationUser {
    return Gson().fromJson(jsonBody, ApplicationUser::class.java)
}

fun jsonToExpense(jsonBody: String): Expense {
    return Gson().fromJson(jsonBody, Expense::class.java)
}

fun jsonToExpenseGroup(jsonBody: String): ExpenseGroup {
    return Gson().fromJson(jsonBody, ExpenseGroup::class.java)
}


fun jsonToLoginAndPassword(jsonBody: String): LoginAndPassword{
    return Gson().fromJson(jsonBody, LoginAndPassword::class.java)
}
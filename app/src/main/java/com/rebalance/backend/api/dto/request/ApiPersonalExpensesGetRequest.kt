package com.rebalance.backend.api.dto.request

import com.google.gson.Gson

data class ApiPersonalExpensesGetRequest(
    val expenseIds: List<Long>
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

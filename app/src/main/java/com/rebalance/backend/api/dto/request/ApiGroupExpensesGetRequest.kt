package com.rebalance.backend.api.dto.request

import com.google.gson.Gson

data class ApiGroupExpensesGetRequest(
    val groupId: Long,
    val expenseIds: List<Long>
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

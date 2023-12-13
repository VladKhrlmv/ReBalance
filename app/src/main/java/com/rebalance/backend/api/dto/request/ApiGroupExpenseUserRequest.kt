package com.rebalance.backend.api.dto.request

import com.google.gson.Gson

data class ApiGroupExpenseUserRequest(
    val userId: Long,
    val multiplier: Int
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

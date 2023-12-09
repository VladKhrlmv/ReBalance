package com.rebalance.backend.api.dto.request

import com.google.gson.Gson

data class ApiGroupAddUserRequest(
    val groupId: Long,
    val email: String
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

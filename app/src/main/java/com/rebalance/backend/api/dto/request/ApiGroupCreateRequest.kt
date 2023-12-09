package com.rebalance.backend.api.dto.request

import com.google.gson.Gson

data class ApiGroupCreateRequest(
    val name: String,
    val currency: String
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

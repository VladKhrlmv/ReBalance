package com.rebalance.backend.api.dto.request

import com.google.gson.Gson

data class ApiRegisterRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val currency: String
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

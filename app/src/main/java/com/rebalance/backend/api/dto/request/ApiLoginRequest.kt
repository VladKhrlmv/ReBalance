package com.rebalance.backend.api.dto.request

import com.google.gson.Gson

data class ApiLoginRequest(
    val email: String,
    val password: String
){
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

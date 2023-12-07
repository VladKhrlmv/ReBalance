package com.rebalance.backend.api

import com.google.gson.Gson
import com.rebalance.backend.api.dto.response.ApiGroupResponse
import com.rebalance.backend.api.dto.response.ApiLoginResponse
import com.rebalance.backend.api.dto.response.ApiRegisterResponse
import com.rebalance.backend.api.dto.response.ApiUserResponse

class RequestParser {
    companion object {
        fun responseToUser(jsonBody: String): ApiUserResponse {
            return Gson().fromJson(jsonBody, ApiUserResponse::class.java)
        }

        fun responseToLogin(jsonBody: String): ApiLoginResponse {
            return Gson().fromJson(jsonBody, ApiLoginResponse::class.java)
        }

        fun responseToRegister(jsonBody: String): ApiRegisterResponse {
            return Gson().fromJson(jsonBody, ApiRegisterResponse::class.java)
        }

        fun responseToGroup(jsonBody: String): ApiGroupResponse {
            return Gson().fromJson(jsonBody, ApiGroupResponse::class.java)
        }
    }
}

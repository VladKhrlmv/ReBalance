package com.rebalance.backend.api

import com.google.gson.Gson
import com.rebalance.backend.api.dto.response.*

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

        fun responseToUserGroup(jsonBody: String): ApiUserGroupResponse {
            return Gson().fromJson(jsonBody, ApiUserGroupResponse::class.java)
        }
    }
}

package com.rebalance.backend.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

        fun responseToGroupList(jsonBody: String): List<ApiGroupResponse> {
            val itemType = object : TypeToken<List<ApiGroupResponse>>() {}.type
            return Gson().fromJson(jsonBody, itemType)
        }

        fun responseToUserList(jsonBody: String): List<ApiUserInGroupResponse> {
            val itemType = object : TypeToken<List<ApiUserInGroupResponse>>() {}.type
            return Gson().fromJson(jsonBody, itemType)
        }

        fun responseToGroupExpenseList(jsonBody: String): ApiGroupExpensesListResponse {
            return Gson().fromJson(jsonBody, ApiGroupExpensesListResponse::class.java)
        }

        fun responseToPersonalExpenseList(jsonBody: String): ApiPersonalExpensesListResponse {
            return Gson().fromJson(jsonBody, ApiPersonalExpensesListResponse::class.java)
        }
    }
}

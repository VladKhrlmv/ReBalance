package com.rebalance.backend.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rebalance.backend.api.dto.adapters.ApiNotificationTypeAdapter
import com.rebalance.backend.api.dto.adapters.LocalDateTimeAdapter
import com.rebalance.backend.api.dto.response.*
import java.time.LocalDateTime

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

        fun responseToGroupExpensePage(jsonBody: String): ApiGroupExpensesPageResponse {
            return Gson().fromJson(jsonBody, ApiGroupExpensesPageResponse::class.java)
        }

        fun responseToPersonalExpensePage(jsonBody: String): ApiPersonalExpensesPageResponse {
            return Gson().fromJson(jsonBody, ApiPersonalExpensesPageResponse::class.java)
        }

        fun responseToNotificationAll(jsonBody: String): List<ApiNotificationResponse> {
            val itemType = object : TypeToken<List<ApiNotificationResponse>>() {}.type
            return GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                .registerTypeAdapter(ApiNotificationType::class.java, ApiNotificationTypeAdapter())
                .create().fromJson(jsonBody, itemType)
        }

        fun responseToGroupExpenseList(jsonBody: String): List<ApiGroupExpenseResponse> {
            val itemType = object : TypeToken<List<ApiGroupExpenseResponse>>() {}.type
            return Gson().fromJson(jsonBody, itemType)
        }

        fun responseToPersonalExpenseList(jsonBody: String): List<ApiPersonalExpenseResponse> {
            val itemType = object : TypeToken<List<ApiPersonalExpenseResponse>>() {}.type
            return Gson().fromJson(jsonBody, itemType)
        }
    }
}

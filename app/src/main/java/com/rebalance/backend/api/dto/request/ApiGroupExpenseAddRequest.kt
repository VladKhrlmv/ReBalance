package com.rebalance.backend.api.dto.request

import com.google.gson.GsonBuilder
import com.rebalance.backend.api.dto.adapters.LocalDateTimeAdapter
import java.math.BigDecimal
import java.time.LocalDateTime

data class ApiGroupExpenseAddRequest(
    val initiatorUserId: Long,
    val groupId: Long,
    val amount: BigDecimal,
    val description: String,
    val category: String,
    val date: LocalDateTime,
    val users: List<ApiGroupExpenseUserRequest>
) {
    fun toJson(): String {
        return GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime::class.java,
                LocalDateTimeAdapter()
            )
            .create().toJson(this)
    }
}

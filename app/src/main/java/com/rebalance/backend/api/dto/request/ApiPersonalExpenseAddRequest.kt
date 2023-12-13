package com.rebalance.backend.api.dto.request

import com.google.gson.GsonBuilder
import com.rebalance.backend.api.dto.adapters.LocalDateTimeAdapter
import java.math.BigDecimal
import java.time.LocalDateTime

data class ApiPersonalExpenseAddRequest(
    val amount: BigDecimal,
    val description: String,
    val category: String,
    val date: LocalDateTime
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

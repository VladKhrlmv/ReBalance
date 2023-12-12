package com.rebalance.backend.api.dto.response

import java.math.BigDecimal
import java.util.*

data class ApiPersonalExpenseResponse(
    val id: Long,
    val amount: BigDecimal,
    val description: String,
    val date: Date,
    val category: String
)

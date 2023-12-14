package com.rebalance.backend.api.dto.response

import java.math.BigDecimal
import java.util.*

data class ApiGroupExpenseResponse(
    val id: Long,
    val initiatorUserId: Long,
    val addedByUserId: Long,
    val amount: BigDecimal,
    val description: String,
    val date: Date,
    val category: String,
    val users: List<ApiGroupExpenseUserResponse>
)

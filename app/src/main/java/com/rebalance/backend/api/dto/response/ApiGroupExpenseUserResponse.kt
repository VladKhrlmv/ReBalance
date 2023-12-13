package com.rebalance.backend.api.dto.response

import java.math.BigDecimal

data class ApiGroupExpenseUserResponse(
    val amount: BigDecimal,
    val multiplier: Int,
    val userId: Long
)

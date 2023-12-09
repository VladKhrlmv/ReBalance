package com.rebalance.backend.api.dto.request

import java.math.BigDecimal

data class ApiGroupExpenseUserRequest(
    val userId: Long,
    val amount: BigDecimal,
    val multiplier: Int
)

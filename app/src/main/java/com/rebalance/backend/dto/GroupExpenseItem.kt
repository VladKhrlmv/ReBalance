package com.rebalance.backend.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class GroupExpenseItem(
    val id: Long,
    val amount: BigDecimal,
    val description: String,
    var date: LocalDateTime,
    val category: String?,
    val initiator: String
)

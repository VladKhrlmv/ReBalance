package com.rebalance.backend.dto

import java.math.BigDecimal
import java.util.*

data class NewGroupSpending(
    val initiatorUserId: Long,
    val groupId: Long,
    val amount: BigDecimal,
    val description: String,
    val category: String,
    val date: Date,
    val users: List<SpendingDeptor>
)

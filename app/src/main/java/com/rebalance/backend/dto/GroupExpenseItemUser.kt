package com.rebalance.backend.dto

import java.math.BigDecimal

data class GroupExpenseItemUser(
    val user: String,
    val amount: BigDecimal
)

package com.rebalance.backend.dto

import java.math.BigDecimal
import java.util.*

data class NewPersonalSpending(
    val amount: BigDecimal,
    val description: String,
    val category: String,
    val date: Date
)

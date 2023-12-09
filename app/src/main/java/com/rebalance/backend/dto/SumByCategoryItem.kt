package com.rebalance.backend.dto

import java.math.BigDecimal

data class SumByCategoryItem(
    val category: String,
    val amount: BigDecimal
)

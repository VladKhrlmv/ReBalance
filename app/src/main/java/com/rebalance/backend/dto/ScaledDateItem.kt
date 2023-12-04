package com.rebalance.backend.dto

import java.time.LocalDateTime

/** Item used for selecting scaled date on personal screen (horizontal navigation) **/
data class ScaledDateItem(
    val name: String,
    val dateFrom: LocalDateTime,
    val dateTo: LocalDateTime
)

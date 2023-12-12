package com.rebalance.backend.api.dto.response

import java.math.BigDecimal

data class ApiUserInGroupResponse(
    val id: Long,
    val nickname: String,
    val email: String,
    val balance: BigDecimal
)

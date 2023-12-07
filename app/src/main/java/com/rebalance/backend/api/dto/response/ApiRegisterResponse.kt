package com.rebalance.backend.api.dto.response

data class ApiRegisterResponse(
    val id: Long,
    val nickname: String,
    val email: String,
    val currency: String,
    val personalGroupId: Long,
    val token: String
)

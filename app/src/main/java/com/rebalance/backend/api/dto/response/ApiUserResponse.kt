package com.rebalance.backend.api.dto.response

data class ApiUserResponse(
    val id: Long,
    val nickname: String,
    val mail: String,
    val personalGroupId: Long
)

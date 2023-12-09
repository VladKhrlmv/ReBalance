package com.rebalance.backend.api.dto.response

data class ApiGroupResponse(
    val id: Long,
    val name: String,
    val currency: String,
    val favorite: Boolean
)

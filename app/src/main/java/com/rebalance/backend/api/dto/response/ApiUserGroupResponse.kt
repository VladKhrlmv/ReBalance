package com.rebalance.backend.api.dto.response

data class ApiUserGroupResponse(
    val id: Long,
    val userId: Long,
    val groupId: Long,
    val favorite: Boolean
)

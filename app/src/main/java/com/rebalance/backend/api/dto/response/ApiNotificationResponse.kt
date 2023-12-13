package com.rebalance.backend.api.dto.response

import java.time.LocalDateTime

data class ApiNotificationResponse(
    val type: ApiNotificationType,
    val initiatorUserId: Long,
    val userAddedId: Long,
    val expenseId: Long,
    val groupId: Long,
    val date: LocalDateTime
)

package com.rebalance.backend.api.dto.response

data class ApiPersonalExpensesPageResponse(
    val content: List<ApiPersonalExpenseResponse>,
    val totalPages: Int
)

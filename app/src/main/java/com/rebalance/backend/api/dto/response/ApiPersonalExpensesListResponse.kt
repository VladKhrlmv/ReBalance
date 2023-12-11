package com.rebalance.backend.api.dto.response

data class ApiPersonalExpensesListResponse(
    val content: List<ApiPersonalExpenseResponse>,
    val totalPages: Int
)

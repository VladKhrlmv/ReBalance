package com.rebalance.backend.api.dto.response

data class ApiGroupExpensesListResponse(
    val content: List<ApiGroupExpenseResponse>,
    val totalPages: Int
)

package com.rebalance.backend.api.dto.response

data class ApiGroupExpensesPageResponse(
    val content: List<ApiGroupExpenseResponse>,
    val totalPages: Int
)

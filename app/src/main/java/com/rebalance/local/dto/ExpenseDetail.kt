package com.rebalance.local.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.rebalance.local.entities.Category
import com.rebalance.local.entities.Expense
import com.rebalance.local.entities.ExpenseUser
import com.rebalance.local.entities.User

data class ExpenseDetail(
    @Embedded val expense: Expense,
    @Relation(
        parentColumn = "added_by",
        entityColumn = "id"
    )
    val userWhoAdded: User,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "expense_id"
    )
    val details: List<ExpenseUser>
)

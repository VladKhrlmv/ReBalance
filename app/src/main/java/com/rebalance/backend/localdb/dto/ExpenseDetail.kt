package com.rebalance.backend.localdb.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.rebalance.backend.localdb.entities.Expense
import com.rebalance.backend.localdb.entities.ExpenseUser
import com.rebalance.backend.localdb.entities.User

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

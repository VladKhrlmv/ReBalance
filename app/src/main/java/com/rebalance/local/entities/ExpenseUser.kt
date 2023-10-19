package com.rebalance.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["user_id", "expense_id"])
data class ExpenseUser(
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "expense_id") val expenseId: Long,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "multiplier") val multiplier: Long
)
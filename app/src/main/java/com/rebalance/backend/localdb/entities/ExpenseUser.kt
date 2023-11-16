package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "expense_user",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"]
        ),
        ForeignKey(
            entity = Expense::class,
            parentColumns = ["id"],
            childColumns = ["expense_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseUser(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "db_id")
    val dbId: Long?,
    @ColumnInfo(name = "changed")
    val changed: Boolean,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "multiplier")
    val multiplier: Int,
    @ColumnInfo(name = "user_id")
    val user_id: Long,
    @ColumnInfo(name = "expense_id")
    val expense_id: Long
)

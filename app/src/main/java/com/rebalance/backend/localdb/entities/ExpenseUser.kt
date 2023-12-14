package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal

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
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(name = "amount")
    val amount: BigDecimal,
    @ColumnInfo(name = "multiplier")
    val multiplier: Int,
    @ColumnInfo(name = "user_id")
    val user_id: Long,
    @ColumnInfo(name = "expense_id")
    val expense_id: Long
)

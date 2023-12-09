package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "images",
    foreignKeys = [
        ForeignKey(
            entity = Expense::class,
            parentColumns = ["id"],
            childColumns = ["expense_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Image(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "expense_id")
    val expenseId: Long,
    @ColumnInfo(name = "changed")
    val changed: Boolean,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "data")
    val data: String
)



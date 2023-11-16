package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Expense(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "currency") val amount: Float,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "added_by") val addedBy: Long,
    @ColumnInfo(name = "group_id") val groupId: Long,
    @ColumnInfo(name = "db_id") val dbId: Long?,
    @ColumnInfo(name = "changed") val changed: Boolean
)

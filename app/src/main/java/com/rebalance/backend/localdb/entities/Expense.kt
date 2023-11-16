package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["initiator_id"]
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["added_by_id"]
        ),
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "db_id")
    val dbId: Long?,
    @ColumnInfo(name = "changed")
    val changed: Boolean,
    @ColumnInfo(name = "currency")
    val amount: Float,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "date")
    val date: LocalDateTime,
    @ColumnInfo(name = "category_id")
    val category_id: Long,
    @ColumnInfo(name = "initiator_id")
    val initiator_id: Long,
    @ColumnInfo(name = "added_by_id")
    val added_by_id: Long,
    @ColumnInfo(name = "group_id")
    val group_id: Long
)

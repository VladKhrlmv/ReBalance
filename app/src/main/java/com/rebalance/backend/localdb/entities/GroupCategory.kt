package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "group_category",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GroupCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "last_used")
    val last_used: LocalDateTime,
    @ColumnInfo(name = "group_id")
    val group_id: Long,
    @ColumnInfo(name = "category_id")
    val category_id: Long
)

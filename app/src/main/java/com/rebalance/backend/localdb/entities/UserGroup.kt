package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "user_group",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"]
        ),
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "balance")
    val balance: BigDecimal = BigDecimal.ZERO,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "group_id")
    val groupId: Long
)

package com.rebalance.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["user_id", "group_id"])
data class UserGroup(
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "group_id") val groupId: Long,
    @ColumnInfo(name = "db_id") val dbId: Long?,
    @ColumnInfo(name = "changed") val changed: Boolean
)

package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "db_id")
    val dbId: Long?,
    @ColumnInfo(name = "changed")
    val changed: Boolean,
    @ColumnInfo(name = "nickname")
    val nickname: String,
    @ColumnInfo(name = "email")
    val email: String
)



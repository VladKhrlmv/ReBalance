package com.rebalance.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "username") val username: String
)



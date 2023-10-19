package com.rebalance.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "name") val name: String
)

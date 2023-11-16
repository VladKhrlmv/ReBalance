package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "db_id")
    val db_id: Long?,
    @ColumnInfo(name = "name")
    val name: String
)

package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "groups"
)
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "changed")
    val changed: Boolean,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "currency")
    val currency: String,
    @ColumnInfo(name = "personal")
    val personal: Boolean
)

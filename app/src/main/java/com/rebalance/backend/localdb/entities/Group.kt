package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "groups",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["creator_id"]
        )
    ]
)
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "db_id")
    val dbId: Long?,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "currency")
    val currency: String,
    @ColumnInfo(name = "personal")
    val personal: Boolean,
    @ColumnInfo(name = "creator_id")
    val creator_id: Long
)

package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "server_ip")
    val server_ip: String,
    @ColumnInfo(name = "user_id")
    val user_id: Long,
    @ColumnInfo(name = "group_ip")
    val group_ip: Long,
    @ColumnInfo(name = "first_launch")
    val first_launch: Boolean,
    @ColumnInfo(name = "token")
    val token: String?
) {
    companion object {
        fun getDefaultInstance(): Settings {
            return Settings(
                id = 1,
                server_ip = "http://26.129.34.140:8080/v1",
                user_id = -1,
                group_ip = -1,
                first_launch = true,
                token = null
            )
        }
    }
}



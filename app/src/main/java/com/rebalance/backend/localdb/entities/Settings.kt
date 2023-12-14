package com.rebalance.backend.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "server_ip")
    val server_ip: String,
    @ColumnInfo(name = "stomp_endpoint")
    val stompEndpoint: String,
    @ColumnInfo(name = "user_id")
    var user_id: Long,
    @ColumnInfo(name = "group_id")
    var group_id: Long,
    @ColumnInfo(name = "first_launch")
    var first_launch: Boolean,
    @ColumnInfo(name = "token")
    var token: String,
    @ColumnInfo(name = "currency")
    var currency: String,
    @ColumnInfo(name = "last_update_date")
    var lastUpdateDate: LocalDateTime,
    @ColumnInfo(name = "curr_notification_channel")
    var currNotificationChannel: String
) {
    companion object {
        fun getDefaultInstance(): Settings {
            return Settings(
                id = 1,
                server_ip = "http://26.129.34.140:8080/v1",
                stompEndpoint = "ws://26.129.34.140:8080",
                user_id = -1,
                group_id = -1,
                first_launch = true,
                token = "",
                currency = "",
                lastUpdateDate = LocalDateTime.ofInstant(Date().toInstant(), ZoneId.of("UTC")),
                "systemChannel"
            )
        }
    }
}

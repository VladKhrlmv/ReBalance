package com.rebalance.backend.localdb.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeConverter {
    @TypeConverter
    fun fromDate(date: LocalDateTime): String {
        return "${date.dayOfMonth}-${date.monthValue}-${date.year}T${date.hour}:${date.minute}:${date.second}"
    }

    @TypeConverter
    fun toDate(date: String): LocalDateTime {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyyTHH:mm:ss"))
    }
}

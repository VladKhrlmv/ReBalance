package com.rebalance.backend.localdb.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeConverter {
    @TypeConverter
    fun fromDate(date: LocalDateTime): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toDate(date: String): LocalDateTime {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}

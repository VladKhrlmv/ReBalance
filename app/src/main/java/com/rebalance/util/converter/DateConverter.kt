package com.rebalance.util.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateConverter {

    @TypeConverter
    fun fromDate(date: LocalDate): String{
        return "${date.dayOfMonth}-${date.monthValue}-${date.year}"
    }

    @TypeConverter
    fun toDate(date: String): LocalDate{
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }

}
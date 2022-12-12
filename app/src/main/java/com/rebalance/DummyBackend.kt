package com.rebalance

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class DummyBackend {
    fun getValues(type: DummyType): List<DummyTypeItem> {
        val list = ArrayList<DummyTypeItem>()

        if (type == DummyType.Day) {
            for (i in 0..30) {
                list.add(DummyTypeItem("Day $i", SimpleDateFormat("yyyy-MM-dd").parse("2022-01-$i")))
            }
        }
        else if (type == DummyType.Week) {
            for (i in 0..20) {
                list.add(DummyTypeItem("Week $i", SimpleDateFormat("yyyy-MM-dd").parse("2022-01-$i")))
            }
        }
        else if (type == DummyType.Month) {
            for (i in 0..12) {
                list.add(DummyTypeItem("Month $i", SimpleDateFormat("yyyy-MM-dd").parse("2022-$i-01")))
            }
        }
        else if (type == DummyType.Year) {
            for (i in 0..100) {
                list.add(DummyTypeItem("Year $i", SimpleDateFormat("yyyy-MM-dd").parse("2$i-01-01")))
            }
        }

        return list
    }

    fun getPersonal(type: DummyType, date: LocalDate): List<DummyItem> {
        val list = ArrayList<DummyItem>()

        for (i in 0..10) {
            list.add(DummyItem("Item type: $type date: $date num: $i"))
        }

        return list
    }
}

enum class DummyType {
    Day,
    Week,
    Month,
    Year
}

data class DummyTypeItem (
    val name: String,
    val date: Date
)

data class DummyItem (
    val name: String
)
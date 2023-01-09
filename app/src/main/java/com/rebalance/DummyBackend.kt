package com.rebalance

import java.text.SimpleDateFormat
import java.util.Date

class DummyBackend {
    fun getValues(scale: DummyScale): List<DummyItem> {
        val list = ArrayList<DummyItem>()

        if (scale == DummyScale.Day) {
            for (i in 0..30) {
                list.add(DummyItem("Day $i", SimpleDateFormat("yyyy-MM-dd").parse("2022-01-$i")))
            }
        }
        else if (scale == DummyScale.Week) {
            for (i in 0..20) {
                list.add(DummyItem("Week $i", SimpleDateFormat("yyyy-MM-dd").parse("2022-01-$i")))
            }
        }
        else if (scale == DummyScale.Month) {
            for (i in 0..12) {
                list.add(DummyItem("Month $i", SimpleDateFormat("yyyy-MM-dd").parse("2022-$i-01")))
            }
        }
        else if (scale == DummyScale.Year) {
            for (i in 0..100) {
                list.add(DummyItem("Year $i", SimpleDateFormat("yyyy-MM-dd").parse("2$i-01-01")))
            }
        }

        return list
    }

    fun getPersonal(scale: DummyScale, date: Date): List<DummyItemValue> {
        val list = ArrayList<DummyItemValue>()

        for (i in 0..50) {
            list.add(DummyItemValue("Item type: $scale date: $date num: $i"))
        }

        return list
    }

    fun getGroup(): List<DummyItemValue> {
        val list = ArrayList<DummyItemValue>()

        for (i in 0..50) {
            list.add(DummyItemValue("Item num: $i"))
        }

        return list
    }

    fun getScale(): List<DummyScaleItem> {
        return listOf(
            DummyScaleItem(DummyScale.Day, "D"),
            DummyScaleItem(DummyScale.Week, "W"),
            DummyScaleItem(DummyScale.Month, "M"),
            DummyScaleItem(DummyScale.Year, "Y")
        )
    }

    fun getGroups(): MutableSet<DummyGroup> {
        return mutableSetOf(
            DummyGroup("Group 1", listOf(
                DummyGroupMember("Member 1"),
                DummyGroupMember("Member 2"),
                DummyGroupMember("Member 3"),
                DummyGroupMember("Member 4")
            )),
            DummyGroup("Group 2", listOf(
                DummyGroupMember("Member 1"),
                DummyGroupMember("Member 2"),
                DummyGroupMember("Member 3")
            )),
            DummyGroup("Group 3", listOf(
                DummyGroupMember("Member 1"),
                DummyGroupMember("Member 2"),
                DummyGroupMember("Member 3"),
                DummyGroupMember("Member 4"),
                DummyGroupMember("Member 5")
            )),
        )
    }
}

enum class DummyScale {
    Day,
    Week,
    Month,
    Year
}

data class DummyScaleItem (
    val type: DummyScale,
    val name: String
)

data class DummyItem (
    val name: String,
    val date: Date
)

data class DummyItemValue (
    val name: String
)

data class DummyGroupMember(
    var name: String
)

data class DummyGroup (
    var name: String,
    var memberList: List<DummyGroupMember>
)
package com.rebalance.backend.service

import com.rebalance.backend.entities.Expense
import java.time.LocalDate
import kotlin.collections.ArrayList

class BackendService {
    //region Personal screen
    /** Returns scale items that is scrollable vertically in personal screen (day, week, month, year) **/
    fun getScaleItems(): List<ScaleItem> {
        return listOf(
            ScaleItem("Day", "D"),
            ScaleItem("Week", "W"),
            ScaleItem("Month", "M"),
            ScaleItem("Year", "Y")
        )
    }

    /** Returns scaled date items that is scrollable horizontally in personal screen **/
    fun getScaledDateItems(scale: String): List<ScaledDateItem> {
        val list = ArrayList<ScaledDateItem>()

        //TODO: get using requests
        when (scale) {
            "Day" -> {
                for (i in 10..30) {
                    list.add(ScaledDateItem("Day $i", LocalDate.parse("2022-01-$i")))
                }
            }
            "Week" -> {
                for (i in 10..20) {
                    list.add(ScaledDateItem("Week $i", LocalDate.parse("2022-01-$i")))
                }
            }
            "Month" -> {
                for (i in 10..12) {
                    list.add(ScaledDateItem("Month $i", LocalDate.parse("2022-$i-01")))
                }
            }
            "Year" -> {
                for (i in 10..99) {
                    list.add(ScaledDateItem("Year $i", LocalDate.parse("20$i-01-01")))
                }
            }
        }

        return list
    }

    /** Returns list of expenses **/
    fun getPersonal(scale: String, date: LocalDate, sumUp: Boolean): List<Expense> {
        val list = ArrayList<Expense>()

        //TODO: get using requests
        //TODO: sum up by category if specified
        for (i in 0..50) {
            list.add(Expense(i.toLong(), i*i, date, scale, -1))
        }

        return list
    }
    //endregion

    //region Add spending screen
    //TODO: change to entities
    fun getGroups(): MutableSet<DummyGroup> {
        //TODO: get using requests
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
    //endregion

    //region Group screen
    fun getGroup(): List<Expense> {
        val list = ArrayList<Expense>()

        //TODO: get using requests
        //TODO: sum up by category if specified
        for (i in 10..29) {
            list.add(Expense(i.toLong(), i*i, LocalDate.parse("2022-01-$i"), "Item $i", -1))
        }

        return list
    }
    //endregion
}

/** Item used for changing scales on personal screen (vertical navigation) **/
data class ScaleItem (
    val type: String,
    val name: String
)

/** Item used for selecting scaled date on personal screen (horizontal navigation) **/
data class ScaledDateItem (
    val name: String,
    val date: LocalDate
)

//TODO: change to ApplicationUser
data class DummyGroupMember(
    var name: String
)

//TODO: change to ExpenseGroup
data class DummyGroup (
    var name: String,
    var memberList: List<DummyGroupMember>
)

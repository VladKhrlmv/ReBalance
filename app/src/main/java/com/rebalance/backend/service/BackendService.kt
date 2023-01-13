package com.rebalance.backend.service

import android.os.StrictMode
import com.rebalance.backend.GlobalVars
import com.rebalance.backend.api.jsonArrayToApplicationUsers
import com.rebalance.backend.api.jsonArrayToExpenseGroups
import com.rebalance.backend.api.jsonArrayToExpenses
import com.rebalance.backend.api.sendGet
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.entities.ExpenseGroup
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

    /** Returns list of expenses grouped by category **/
    fun getPersonal(scale: String, date: LocalDate): List<ExpenseItem> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val list = ArrayList<ExpenseItem>()

        val jsonBodyGet = sendGet(
            "http://${GlobalVars.serverIp}/groups/${GlobalVars.group.getId()}/expenses"
        )
        val listExpense: List<Expense> = jsonArrayToExpenses(jsonBodyGet)
        val categoryMap: HashMap<String, ExpenseItem> = HashMap()
        listExpense.forEach { entry ->
            if (categoryMap.containsKey(entry.getCategory())) {
                val item = categoryMap.getValue(entry.getCategory())
                item.amount = item.amount + entry.getAmount()
                item.expenses.add(entry)
                categoryMap[entry.getCategory()] = item
            } else {
                categoryMap[entry.getCategory()] = ExpenseItem(entry)
            }
        }
        for (entry in categoryMap.values) {
            list.add(entry)
        }

        return list
    }
    //endregion

    //region Add spending screen
    //TODO: change to entities
    fun getGroups(): List<ExpenseGroup> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val jsonBodyGroups = sendGet(
            "http://${GlobalVars.serverIp}/users/${GlobalVars.user.getId()}/groups"
        )
        val groups: List<ExpenseGroup> = jsonArrayToExpenseGroups(jsonBodyGroups)
        println(groups)

        //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
        return groups
    }
    //endregion

    //region Group screen
    fun getGroupVisualBarChart(groupId: Long): List<BarChartData> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val entries = ArrayList<BarChartData>()

        val jsonBodyGetUsersFromGroup = if (groupId == -1L)  "[]" else sendGet(
            //todo change to group choice
            "http://${GlobalVars.serverIp}/groups/${groupId}/users"
        )
        val userExpenseMap: HashMap<String, Int> = HashMap()

        val userList = if (groupId == -1L)  listOf() else jsonArrayToApplicationUsers(jsonBodyGetUsersFromGroup)
        println(userList)
        for (user in userList) {
            val jsonBodyGet = sendGet(
                //todo change to group choice
                "http://${GlobalVars.serverIp}/groups/${groupId}/users/${user.getId()}/expenses"
            )
            val listExpense: List<Expense> = jsonArrayToExpenses(jsonBodyGet)
            var sumForUser: Int = 0
            for (expense in listExpense) {
                sumForUser += expense.getAmount()
            }
            userExpenseMap[user.getUsername()] = sumForUser
        }
        for (entry in userExpenseMap.entries.iterator()) {
            entries.add(BarChartData(entry.key, entry.value.toDouble() / 100))
        }
        //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
        return entries
    }

    fun getGroupList(groupId: Long): List<Expense> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val responseGroupList = if(groupId == -1L) "[]" else sendGet(
            //todo change to group choice
            "http://${GlobalVars.serverIp}/groups/${groupId}/expenses"
        )

        return jsonArrayToExpenses(responseGroupList).filter { it.getAmount() > 0 }
    }
    //endregion
}

//region Personal screen
/** Item used for changing scales on personal screen (vertical navigation) **/
data class ScaleItem(
    val type: String,
    val name: String
)

/** Item used for selecting scaled date on personal screen (horizontal navigation) **/
data class ScaledDateItem(
    val name: String,
    val date: LocalDate
)

data class ExpenseItem(
    var text: String,
    var amount: Double,
    var expenses: ArrayList<Expense>
) {
    constructor(expense: Expense) : this(
        expense.getCategory(),
        expense.getAmount().toDouble(),
        arrayListOf(expense)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpenseItem

        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }
}
//endregion

//region Group screen
data class BarChartData(
    var debtor: String,
    var value: Double
)
//endregion

package com.rebalance.backend.service

import android.os.StrictMode
import com.rebalance.PreferencesData
import com.rebalance.backend.api.*
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.entities.ExpenseGroup
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class BackendService(
    private val preferences: PreferencesData
) {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val jsonBodyExpenses = sendGet(
            "http://${preferences.serverIp}/groups/${preferences.groupId}/expenses"
        )
        val expenses: List<Expense> = jsonArrayToExpenses(jsonBodyExpenses)

        val list = ArrayList<ScaledDateItem>()

        when (scale) {
            "Day" -> {
                for (ex in expenses) {
                    val date = LocalDate.parse(ex.getDateStamp(), formatter)
                    if (list.any {
                            it.dateTo.year == date.year &&
                                    it.dateTo.dayOfYear == date.dayOfYear }) {
                        continue // if not the same date
                    }
                    list.add(ScaledDateItem(ex.getDateStamp(), date, date))
                }
            }
            "Week" -> {
                for (ex in expenses) {
                    val date = LocalDate.parse(ex.getDateStamp(), formatter)
                    if (list.any {
                            it.dateTo.year == date.year &&
                                    date.dayOfYear - it.dateFrom.dayOfYear < 7 &&
                                    it.dateTo.dayOfYear - date.dayOfYear < 7 &&
                                    date.dayOfWeek >= it.dateFrom.dayOfWeek &&
                                    date.dayOfWeek <= it.dateTo.dayOfWeek }) {
                        continue // if not the same week
                    }
                    val dateFrom = date.with(DayOfWeek.MONDAY)
                    val dateTo = date.with(DayOfWeek.SUNDAY)
                    list.add(ScaledDateItem(dateFrom.format(formatter) +
                            "\n" +
                            dateTo.format(formatter), dateFrom, dateTo))
                }
            }
            "Month" -> {
                for (ex in expenses) {
                    val date = LocalDate.parse(ex.getDateStamp(), formatter)
                    if (list.any {
                            it.dateFrom.year == date.year &&
                                    it.dateFrom.month == date.month }) {
                        continue // if not the same year
                    }
                    val from = date.minusDays(date.dayOfMonth.toLong() - 1)
                    val to = date.plusMonths(1).minusDays(date.dayOfMonth.toLong())
                    list.add(ScaledDateItem(date.month.toString() + " " + date.year.toString(),
                        from, to))
                }
            }
            "Year" -> {
                for (ex in expenses) {
                    val date = LocalDate.parse(ex.getDateStamp(), formatter)
                    if (list.any { it.dateFrom.year == date.year }) {
                        continue // if not the same year
                    }
                    val year = LocalDate.parse(date.year.toString() + "-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    list.add(ScaledDateItem(year.year.toString(), year, year.plusYears(1).minusDays(1)))
                }
            }
        }

        if (list.isEmpty()) { // if empty list add current date otherwise app crash
            list.add(ScaledDateItem(LocalDate.now().format(formatter), LocalDate.now(), LocalDate.now()))
        }

        return list.sortedWith(compareBy({ it.dateFrom.year }, { it.dateFrom.dayOfYear }))
    }

    /** Returns list of expenses grouped by category **/
    fun getPersonal(dateFrom: LocalDate, dateTo: LocalDate): List<ExpenseItem> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val list = ArrayList<ExpenseItem>()

        val jsonBodyGet = sendGet(
            "http://${preferences.serverIp}/expenses/group/${preferences.groupId}/between/${dateFrom.format(
                formatter)}/${dateTo.format(
                formatter)}"
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
            "http://${preferences.serverIp}/users/${preferences.userId}/groups"
        )
        val groups: List<ExpenseGroup> = jsonArrayToExpenseGroups(jsonBodyGroups)
        println(groups)

        //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
        return groups
    }
    //endregion

    //region Group by id
    fun getGroupById(id: Long): ExpenseGroup {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val jsonBodyGroup = sendGet(
            "http://${preferences.serverIp}/groups/${id}"
        )

        //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
        return jsonToExpenseGroup(jsonBodyGroup)
    }
    //endregion

    //region Group screen
    fun getGroupVisualBarChart(groupId: Long): List<BarChartData> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val entries = ArrayList<BarChartData>()

        val jsonBodyGetUsersFromGroup = if (groupId == -1L) "[]" else sendGet(
            //todo change to group choice
            "http://${preferences.serverIp}/groups/${groupId}/users"
        )
        val userExpenseMap: HashMap<String, Double> = HashMap()

        val userList =
            if (groupId == -1L) listOf() else jsonArrayToApplicationUsers(jsonBodyGetUsersFromGroup)
        println(userList)
        for (user in userList) {
            val jsonBodyGet = sendGet(
                //todo change to group choice
                "http://${preferences.serverIp}/groups/${groupId}/users/${user.getId()}/expenses"
            )
            val listExpense: List<Expense> = jsonArrayToExpenses(jsonBodyGet)
            var sumForUser = 0.0
            for (expense in listExpense) {
                sumForUser += expense.getAmount()
            }
            userExpenseMap[user.getUsername()] = sumForUser
        }
        for (entry in userExpenseMap.entries.iterator()) {
            entries.add(BarChartData(entry.key, entry.value))
        }
        //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
        return entries.sortedByDescending { it.debtor }
    }

    fun getGroupList(groupId: Long): List<Expense> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val responseGroupList = if (groupId == -1L) "[]" else sendGet(
            //todo change to group choice
            "http://${preferences.serverIp}/groups/${groupId}/expenses"
        )

        return jsonArrayToExpenses(responseGroupList).filter { it.getAmount() > 0 }.sortedBy {
            LocalDate.parse(it.getDateStamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) }.reversed()
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
    var name: String,
    var dateFrom: LocalDate,
    var dateTo: LocalDate
)

data class ExpenseItem(
    var text: String,
    var amount: Double,
    var expenses: ArrayList<Expense>
) {
    constructor(expense: Expense) : this(
        expense.getCategory(),
        expense.getAmount(),
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

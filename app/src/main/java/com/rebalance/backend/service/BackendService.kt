package com.rebalance.backend.service

import android.content.Context
import android.os.Parcelable
import android.os.StrictMode
import com.rebalance.backend.api.RequestParser
import com.rebalance.backend.api.RequestSender
import com.rebalance.backend.api.dto.request.ApiLoginRequest
import com.rebalance.backend.api.dto.request.ApiRegisterRequest
import com.rebalance.backend.dto.LoginResult
import com.rebalance.backend.dto.RegisterResult
import com.rebalance.backend.localdb.db.AppDatabase
import com.rebalance.backend.localdb.entities.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BackendService {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val mainScope = CoroutineScope(Dispatchers.Main)

    private lateinit var db: AppDatabase
    private lateinit var settings: Settings
    private lateinit var requestSender: RequestSender

    companion object {
        @Volatile
        private var INSTANCE: BackendService? = null

        // ensure that only one instance is used in all composables
        fun get(): BackendService =
            INSTANCE ?: synchronized(this) {
                BackendService().also { INSTANCE = it }
            }
    }

    suspend fun initialize(context: Context, onInit: () -> Unit) {
        // initialize db
        this.db = AppDatabase.getDatabase(context)
        mainScope.launch {
            // try load settings
            var settingsFromDB = withContext(Dispatchers.IO) {
                db.settingsDao().getSettings()
            }
            // if first launch (no settings), save default to db and read them
            if (settingsFromDB == null) {
                withContext(Dispatchers.IO) {
                    db.settingsDao().saveSettings(Settings.getDefaultInstance())
                }
                settingsFromDB = withContext(Dispatchers.IO) {
                    db.settingsDao().getSettings()
                }
            }
            // initialize settings from db and requests sender
            settings = settingsFromDB as Settings
            requestSender = RequestSender(settings.server_ip, settings.token)
            onInit()
        }
    }

    //region private functions
    private suspend fun updateUser(userId: Long, personalGroupId: Long, token: String) {
        this.settings.user_id = userId
        this.settings.group_ip = personalGroupId
        this.settings.token = token
        mainScope.launch {
            withContext(Dispatchers.IO) {
                db.settingsDao().saveSettings(settings)
            }
        }
    }
    //endregion

    //region settings
    fun getUserId(): Long {
        return settings.user_id
    }

    fun getGroupId(): Long {
        return settings.group_ip
    }

    fun isFirstLaunch(): Boolean {
        return settings.first_launch
    }
    //endregion

    //region connection
    suspend fun checkLogin(): LoginResult {
        val (responseCode, responseBody) = requestSender.sendGet("/user/info")
        return when (responseCode) {
            200 -> {
                val user = RequestParser.responseToUser(responseBody)
                if (user.id == settings.user_id &&
                    user.personalGroupId == settings.group_ip
                ) {
                    LoginResult.LoggedIn
                } else LoginResult.TokenInspired
            }
            401 -> LoginResult.TokenInspired
            else -> LoginResult.ServerUnreachable
        }
    }
    //endregion

    //region user
    suspend fun login(request: ApiLoginRequest): LoginResult {
        val (responseCodeLogin, responseBodyLogin) = requestSender.sendPost(
            "/user/login",
            request.toJson()
        )
        return when (responseCodeLogin) {
            200 -> {
                // if login successful, set new token to RequestSender
                val token = RequestParser.responseToLogin(responseBodyLogin).token
                this.requestSender.token = token

                // get user info
                val (responseCodeInfo, responseBodyInfo) = requestSender.sendGet("/user/info")
                if (responseCodeInfo != 200) {
                    // if (somehow) fail, remove token and return BadCredentials
                    this.requestSender.token = ""
                    return LoginResult.TokenInspired
                }

                // update settings in db
                val user = RequestParser.responseToUser(responseBodyInfo)
                updateUser(user.id, user.personalGroupId, token)

                LoginResult.LoggedIn
            }
            400 -> LoginResult.BadCredentials
            else -> LoginResult.ServerUnreachable
        }
    }

    suspend fun register(request: ApiRegisterRequest): RegisterResult {
        val (responseCode, responseBody) = requestSender.sendPost(
            "/user/register",
            request.toJson()
        )
        return when (responseCode) {
            201 -> {
                // update settings in db
                val user = RequestParser.responseToRegister(responseBody)
                updateUser(user.id, user.personalGroupId, user.token)

                RegisterResult.Registered
            }
            400 -> RegisterResult.IncorrectData
            409 -> RegisterResult.EmailAlreadyTaken
            else -> RegisterResult.ServerError
        }
    }
    //endregion

    fun setPolicy() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

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
        setPolicy()

        val jsonBodyExpenses = RequestsSender.sendGet(
            "http://${settings.server_ip}/groups/${settings.group_ip}/expenses"
        )
        val expenses: List<Expense> = jsonArrayToExpenses(jsonBodyExpenses)

        val list = ArrayList<ScaledDateItem>()

        when (scale) {
            "Day" -> {
                for (ex in expenses) {
                    val date = LocalDate.parse(ex.getDateStamp(), formatter)
                    if (list.any {
                            it.dateTo.year == date.year &&
                                    it.dateTo.dayOfYear == date.dayOfYear
                        }) {
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
                                    date.dayOfWeek <= it.dateTo.dayOfWeek
                        }) {
                        continue // if not the same week
                    }
                    val dateFrom = date.with(DayOfWeek.MONDAY)
                    val dateTo = date.with(DayOfWeek.SUNDAY)
                    list.add(
                        ScaledDateItem(
                            dateFrom.format(formatter) +
                                    "\n" +
                                    dateTo.format(formatter), dateFrom, dateTo
                        )
                    )
                }
            }
            "Month" -> {
                for (ex in expenses) {
                    val date = LocalDate.parse(ex.getDateStamp(), formatter)
                    if (list.any {
                            it.dateFrom.year == date.year &&
                                    it.dateFrom.month == date.month
                        }) {
                        continue // if not the same year
                    }
                    val from = date.minusDays(date.dayOfMonth.toLong() - 1)
                    val to = date.plusMonths(1).minusDays(date.dayOfMonth.toLong())
                    list.add(
                        ScaledDateItem(
                            date.month.toString() + " " + date.year.toString(),
                            from, to
                        )
                    )
                }
            }
            "Year" -> {
                for (ex in expenses) {
                    val date = LocalDate.parse(ex.getDateStamp(), formatter)
                    if (list.any { it.dateFrom.year == date.year }) {
                        continue // if not the same year
                    }
                    val year = LocalDate.parse(
                        date.year.toString() + "-01-01",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    )
                    list.add(
                        ScaledDateItem(
                            year.year.toString(),
                            year,
                            year.plusYears(1).minusDays(1)
                        )
                    )
                }
            }
        }

        if (list.isEmpty()) { // if empty list add current date otherwise app crash
            var name = ""
            val date = LocalDate.now()
            when (scale) {
                "Day" -> {
                    name = date.format(formatter)
                }
                "Week" -> {
                    val dateFrom = date.with(DayOfWeek.MONDAY)
                    val dateTo = date.with(DayOfWeek.SUNDAY)
                    name = dateFrom.format(formatter) + "\n" + dateTo.format(formatter)
                }
                "Month" -> {
                    name = date.month.toString() + " " + date.year.toString()
                }
                "Year" -> {
                    name = date.year.toString()
                }
            }

            list.add(
                ScaledDateItem(
                    name,
                    LocalDate.now(),
                    LocalDate.now()
                )
            )
        }

        return list.sortedWith(compareBy({ it.dateFrom.year }, { it.dateFrom.dayOfYear }))
    }

    /** Returns list of expenses grouped by category **/
    fun getPersonal(dateFrom: LocalDate, dateTo: LocalDate): List<ExpenseItem> {
        setPolicy()

        val list = ArrayList<ExpenseItem>()

        val jsonBodyGet = RequestsSender.sendGet(
            "http://${settings.server_ip}/expenses/group/${settings.group_ip}/between/${
                dateFrom.format(
                    formatter
                )
            }/${
                dateTo.format(
                    formatter
                )
            }"
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
    fun getGroups(userId: Long? = null): List<ExpenseGroup> {
        setPolicy()

        val jsonBodyGroups = RequestsSender.sendGet(
            "http://${settings.server_ip}/users/${userId ?: settings.user_id}/groups"
        )
        val groups: List<ExpenseGroup> = jsonArrayToExpenseGroups(jsonBodyGroups)

        //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
        return groups
    }

    fun addExpense(expense: Expense, groupId: Long, userId: Long? = null): Expense {
        setPolicy()
        val jsonBodyPOST = RequestsSender.sendPost(
            "http://${settings.server_ip}/expenses/user/${userId ?: settings.user_id}/group/${groupId}/${settings.user_id}",
            Gson().toJson(expense)
        )
        return jsonToExpense(jsonBodyPOST)
    }

    fun addExpenseImage(imageBase64String: String, expenseGlobalId: Long?) {
        val body = """{"image": "$imageBase64String"}""".replace("\n", "")
        RequestsSender.sendPost(
            "http://${settings.server_ip}/expenses/${expenseGlobalId}/image",
            body
        )
    }
    //endregion

    //region Group by id
    fun getGroupById(id: Long): ExpenseGroup {
        setPolicy()

        val jsonBodyGroup = RequestsSender.sendGet(
            "http://${settings.server_ip}/groups/${id}"
        )

        //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
        return jsonToExpenseGroup(jsonBodyGroup)
    }
    //endregion

    //region Group screen
    fun getGroupVisualBarChart(groupId: Long): List<BarChartData> {
        setPolicy()
        val entries = ArrayList<BarChartData>()

        val jsonBodyGetUsersFromGroup = if (groupId == -1L) "[]" else RequestsSender.sendGet(
            //todo change to group choice
            "http://${settings.server_ip}/groups/${groupId}/users"
        )
        val userExpenseMap: HashMap<Long, Pair<String, Double>> = HashMap()

        val userList =
            if (groupId == -1L) listOf() else jsonArrayToApplicationUsers(jsonBodyGetUsersFromGroup)
        println(userList)
        for (user in userList) {
            val jsonBodyGet = RequestsSender.sendGet(
                //todo change to group choice
                "http://${settings.server_ip}/groups/${groupId}/users/${user.getId()}/expenses"
            )
            val listExpense: List<Expense> = jsonArrayToExpenses(jsonBodyGet)
            var sumForUser = 0.0
            for (expense in listExpense) {
                sumForUser += expense.getAmount()
            }
            userExpenseMap[user.getId()] = Pair(user.getUsername(), sumForUser)
        }
        for (entry in userExpenseMap.entries.iterator()) {
            entries.add(BarChartData(entry.key, entry.value))
        }
        //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
        return entries.sortedByDescending { it.data.first }
    }

    fun getGroupList(groupId: Long): List<Expense> {
        setPolicy()

        val responseGroupList = if (groupId == -1L) "[]" else RequestsSender.sendGet(
            //todo change to group choice
            "http://${settings.server_ip}/groups/${groupId}/expenses"
        )

        return jsonArrayToExpenses(responseGroupList).sortedBy {
            LocalDate.parse(it.getDateStamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }.reversed()
    }

    fun getExpensePicture(globalId: Long?): ByteArray? {
        setPolicy()
        if (globalId == null) {
            return null
        }

        return try {
            val responseJson = RequestsSender.sendGet(
                "http://${settings.server_ip}/expenses/${globalId}/image"
            )
            println(responseJson)
            val imageClass: ExpenseImage = Gson().fromJson(responseJson, ExpenseImage::class.java)
            Base64.decode(imageClass.getImage(), Base64.DEFAULT)
        } catch (e: ServerException) {
            println(e.message)
            null
        }
    }

    fun getExpenseIcon(globalId: Long?): ByteArray? {
        setPolicy()
        if (globalId == null) {
            return null
        }

        return try {
            val responseJson = RequestsSender.sendGet(
                "http://${settings.server_ip}/expenses/${globalId}/icon"
            )
            println(responseJson)
            val imageClass: ExpenseImage = Gson().fromJson(responseJson, ExpenseImage::class.java)
            Base64.decode(imageClass.getImage(), Base64.DEFAULT)
        } catch (e: ServerException) {
            println(e.message)
            null
        }
    }

    fun deleteExpenseByGlobalId(globalId: Long?) {
        setPolicy()
        RequestsSender.sendDelete(
            "http://${settings.server_ip}/expenses/${globalId}"
        )
    }

    fun createGroup(groupCurrency: String, groupName: String, userId: Long? = null): ExpenseGroup {
        setPolicy()
        val responseJson = RequestsSender.sendPost(
            "http://${settings.server_ip}/users/${userId ?: settings.user_id}/groups",
            "{\"currency\": \"${groupCurrency}\", \"name\": \"${groupName}\"}"
        )
        return jsonToExpenseGroup(responseJson)
    }

    fun addUserToGroup(userId: Long, groupId: Long) {
        setPolicy()
        RequestsSender.sendPost(
            "http://${settings.server_ip}/users/${userId}/groups",
            "{\"id\": ${groupId}}"
        )
    }
    //endregion

    //region Notifications
    fun getNotifications(): List<Notification> {
        setPolicy()
        val responseJson = RequestsSender.sendGet(
            "http://${settings.server_ip}/users/${
                settings.user_id
            }/notifications"
        )
        return jsonArrayToNotification(responseJson)
    }
    //endregion

    //region Login
    fun login(email: String, password: String): ApplicationUser {
        setPolicy()
        val responseJson = RequestsSender.sendPost(
            "http://${settings.server_ip}/users/login",
            Gson().toJson(LoginAndPassword(email, password))
        )
        return jsonToApplicationUser(responseJson)
    }

    fun register(email: String, username: String, password: String): LoginAndPassword {
        setPolicy()
        val responseJson = RequestsSender.sendPost(
            "http://${settings.server_ip}/users",
            Gson().toJson(ApplicationUser(username, email, password))
        )
        return jsonToLoginAndPassword(responseJson)
    }

    fun getUserByEmail(email: String): ApplicationUser {
        setPolicy()
        val responseJson =
            RequestsSender.sendGet("http://${settings.server_ip}/users/email/${email}")
        return jsonToApplicationUser(responseJson)
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
@Parcelize
data class ScaledDateItem(
    var name: String,
    var dateFrom: LocalDate,
    var dateTo: LocalDate
) : Parcelable

@Parcelize
data class ExpenseItem(
    var text: String,
    var amount: Double,
    var expenses: ArrayList<Expense>
) : Parcelable {
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
        if (amount != other.amount) return false
        if (expenses != other.expenses) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + expenses.hashCode()
        return result
    }
}
//endregion

//region Group screen
data class BarChartData(
    var id: Long,
    var data: Pair<String, Double>
)
//endregion

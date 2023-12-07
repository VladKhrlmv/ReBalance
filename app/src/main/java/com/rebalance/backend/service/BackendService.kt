package com.rebalance.backend.service

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import com.rebalance.backend.api.RequestParser
import com.rebalance.backend.api.RequestSender
import com.rebalance.backend.api.dto.request.ApiGroupCreateRequest
import com.rebalance.backend.api.dto.request.ApiLoginRequest
import com.rebalance.backend.api.dto.request.ApiRegisterRequest
import com.rebalance.backend.dto.*
import com.rebalance.backend.localdb.db.AppDatabase
import com.rebalance.backend.localdb.entities.*
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
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

    //region updating settings in db
    private suspend fun updateUserInSettings(userId: Long, personalGroupId: Long, token: String) {
        this.settings.user_id = userId
        this.settings.group_ip = personalGroupId
        this.settings.token = token
        mainScope.async {
            withContext(Dispatchers.IO) {
                db.settingsDao().saveSettings(settings)
            }
            return@async
        }.await()
    }

    suspend fun updateFirstLaunch(firstLaunch: Boolean): Boolean {
        this.settings.first_launch = firstLaunch
        return mainScope.async {
            withContext(Dispatchers.IO) {
                db.settingsDao().saveSettings(settings)
            }
            return@async settings.first_launch
        }.await()
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
        // when no token, go to auth screen
        if (settings.token.isEmpty()) return LoginResult.TokenInspired
        val (responseCode, _) = requestSender.sendGet("/user/info")
        return when (responseCode) {
            200 -> LoginResult.LoggedIn
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
    suspend fun getTabItems(scale: String): List<ScaledDateItem> {
        val list = ArrayList<ScaledDateItem>()

        when (scale) {
            "Day" -> {
                // get unique dates from db
                val dates = mainScope.async {
                    var dates: List<LocalDateTime>
                    withContext(Dispatchers.IO) {
                        dates = db.expenseDao().getUniqueExpenseDays(settings.group_ip)
                    }
                    return@async dates
                }.await()
                // add dates to return list
                dates.forEach { d ->
                    list.add(
                        ScaledDateItem(
                            d.format(formatter),
                            d,
                            d
                        )
                    )
                }
            }
            "Week" -> {
                // get unique years from db
                val dates = mainScope.async {
                    var dates: List<String>
                    withContext(Dispatchers.IO) {
                        dates = db.expenseDao().getUniqueExpenseWeeks(settings.group_ip)
                    }
                    return@async dates
                }.await()
                // add dates to return list
                dates.forEach { d ->
                    val startDate =
                        LocalDate.parse(d, DateTimeFormatter.ofPattern("yyyy-WW")).atStartOfDay()
                    val endDate = startDate.plusWeeks(1).minusSeconds(1)
                    list.add(
                        ScaledDateItem(
                            startDate.format(formatter),
                            startDate,
                            endDate
                        )
                    )
                }
            }
            "Month" -> {
                // get unique years from db
                val dates = mainScope.async {
                    var dates: List<String>
                    withContext(Dispatchers.IO) {
                        dates = db.expenseDao().getUniqueExpenseMonths(settings.group_ip)
                    }
                    return@async dates
                }.await()
                // add dates to return list
                dates.forEach { d ->
                    val startDate =
                        LocalDate.parse(d, DateTimeFormatter.ofPattern("yyyy-MM")).atStartOfDay()
                    val endDate = startDate.plusMonths(1).minusSeconds(1)
                    list.add(
                        ScaledDateItem(
                            startDate.month.toString() + " " + startDate.year.toString(),
                            startDate,
                            endDate
                        )
                    )
                }
            }
            "Year" -> {
                // get unique years from db
                val dates = mainScope.async {
                    var dates: List<Int>
                    withContext(Dispatchers.IO) {
                        dates = db.expenseDao().getUniqueExpenseYears(settings.group_ip)
                    }
                    return@async dates
                }.await()
                // add dates to return list
                dates.forEach { d ->
                    val startYear = LocalDateTime.of(d, 1, 1, 0, 0, 0)
                    list.add(
                        ScaledDateItem(
                            d.toString(),
                            startYear,
                            startYear.plusYears(1).minusSeconds(1)
                        )
                    )
                }
            }
        }

        if (list.isEmpty()) { // if empty list add current date otherwise app crash
            var name = ""
            val date = LocalDateTime.now()
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
                    LocalDateTime.now(),
                    LocalDateTime.now()
                )
            )
        }

        return list.sortedWith(
            compareBy(
                { it.dateFrom.year },
                { it.dateFrom.dayOfYear })
        )
    }

    /** Returns categories and their sums in the provided dates range **/
    suspend fun getPieChartData(scaledDateItem: ScaledDateItem): List<SumByCategoryItem> {
        return mainScope.async {
            var sums: List<SumByCategoryItem>
            withContext(Dispatchers.IO) {
                sums = db.expenseDao().getSumsByCategories(
                    settings.group_ip,
                    scaledDateItem.dateFrom,
                    scaledDateItem.dateTo
                )
            }
            return@async sums
        }.await()
    }

    /** Returns list of expenses grouped by category in the provided dates range **/
    suspend fun getExpensesByCategory(category: String, scaledDateItem: ScaledDateItem): List<Expense> {
        return mainScope.async {
            var sums: List<Expense>
            withContext(Dispatchers.IO) {
                sums = db.expenseDao().getExpensesByCategory(settings.group_ip, category, scaledDateItem.dateFrom, scaledDateItem.dateTo)
            }
            return@async sums
        }.await()
    }

    /** Deletes personal expense from localdb and server, returns result of an operation **/
    suspend fun deletePersonalExpenseById(expenseId: Long): DeleteResult {
        // delete from server
        val (responseCode, _) = requestSender.sendDelete("/personal/expenses/$expenseId")
        return when (responseCode) {
            204 -> {
                // id successful, delete from localdb
                mainScope.async {
                    withContext(Dispatchers.IO) {
                        db.expenseDao().deleteById(expenseId)
                    }
                    return@async
                }.await()
                DeleteResult.Deleted
            }
            //TODO: correctly handle other cases
            404 -> DeleteResult.NotFound
            409 -> DeleteResult.IncorrectId
            else -> DeleteResult.ServerError
        }
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

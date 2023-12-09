package com.rebalance.backend.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.rebalance.backend.api.RequestParser
import com.rebalance.backend.api.RequestSender
import com.rebalance.backend.api.dto.request.ApiGroupCreateRequest
import com.rebalance.backend.api.dto.request.ApiLoginRequest
import com.rebalance.backend.api.dto.request.ApiRegisterRequest
import com.rebalance.backend.dto.*
import com.rebalance.backend.localdb.db.AppDatabase
import com.rebalance.backend.localdb.entities.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
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
    private suspend fun updateCurrentUser(
        userId: Long,
        nickname: String,
        email: String,
        personalGroupId: Long,
        currency: String
    ) {
        mainScope.async {
            // save/update user
            val user = User(userId, nickname, email)
            withContext(Dispatchers.IO) {
                db.userDao().save(user)
            }
            // save/update group
            val personalGroup = Group(personalGroupId, false, "personal_$email", currency, true)
            withContext(Dispatchers.IO) {
                db.groupDao().saveGroup(personalGroup)
            }
            // save/update UserGroup relation
            val userGroup = UserGroup(0, false, BigDecimal.ZERO, userId, personalGroupId)
            withContext(Dispatchers.IO) {
                db.userGroupDao().saveUserGroup(userGroup)
            }
            return@async
        }.await()
    }

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
                updateUserInSettings(user.id, user.personalGroupId, token)

                // update current user in db
                updateCurrentUser(
                    user.id,
                    user.nickname,
                    user.email,
                    user.personalGroupId,
                    user.currency
                )

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
                updateUserInSettings(
                    user.id,
                    user.personalGroupId,
                    user.token
                )

                // update user in db
                updateCurrentUser(
                    user.id,
                    user.nickname,
                    user.email,
                    user.personalGroupId,
                    user.currency
                )

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
    suspend fun getExpensesByCategory(
        category: String,
        scaledDateItem: ScaledDateItem
    ): List<Expense> {
        return mainScope.async {
            var sums: List<Expense>
            withContext(Dispatchers.IO) {
                sums = db.expenseDao().getExpensesByCategory(
                    settings.group_ip,
                    category,
                    scaledDateItem.dateFrom,
                    scaledDateItem.dateTo
                )
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
                // if successful, delete from localdb
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

    //region Group screen
    suspend fun getGroupById(groupId: Long): Group? {
        return mainScope.async {
            var group: Group?
            withContext(Dispatchers.IO) {
                group = db.groupDao().getGroupById(groupId)
            }
            return@async group
        }.await()
    }

    suspend fun getUserGroups(): List<Group> {
        return mainScope.async {
            var groups: List<Group>
            withContext(Dispatchers.IO) {
                groups = db.groupDao().getUserGroups(settings.user_id)
            }
            return@async groups
        }.await()
    }

    suspend fun createGroup(request: ApiGroupCreateRequest): Long {
        // send request to create new group
        val (responseCode, responseBody) = requestSender.sendPost(
            "/group",
            request.toJson()
        )
        return when (responseCode) {
            201 -> { //TODO: check if server available
                val groupResponse = RequestParser.responseToGroup(responseBody)
                // save new group in db
                val group = Group(
                    groupResponse.id,
                    false,
                    groupResponse.name,
                    groupResponse.currency,
                    false
                )
                mainScope.async {
                    withContext(Dispatchers.IO) {
                        db.groupDao().saveGroup(group)
                    }
                    return@async
                }.await()
                // save UserGroup with current user in db
                val userGroup = UserGroup(
                    0L,
                    groupResponse.favorite,
                    BigDecimal.ZERO,
                    settings.user_id,
                    groupResponse.id
                )
                mainScope.async {
                    withContext(Dispatchers.IO) {
                        db.userGroupDao().saveUserGroup(userGroup)
                    }
                    return@async
                }.await()

                return group.id
            }
            else -> -1L
        }
    }

    suspend fun getBarChartData(groupId: Long): List<BarChartItem> {
        return mainScope.async {
            val balances: List<BarChartItem>
            withContext(Dispatchers.IO) {
                balances = db.userGroupDao().getUserBalancesForGroup(groupId)
            }
            return@async balances
        }.await()
    }

    suspend fun getGroupExpenses(groupId: Long, offset: Int): List<GroupExpenseItem> {
        return mainScope.async {
            val expenses: List<GroupExpenseItem>
            withContext(Dispatchers.IO) {
                expenses = db.expenseDao().getGroupExpenses(groupId, offset)
            }
            return@async expenses
        }.await()
    }

    suspend fun getGroupExpenseDeptors(expenseId: Long): List<GroupExpenseItemUser> {
        return mainScope.async {
            val expenseUsers: List<GroupExpenseItemUser>
            withContext(Dispatchers.IO) {
                expenseUsers = db.expenseUserDao().getGroupExpenseDeptors(expenseId)
            }
            return@async expenseUsers
        }.await()
    }

    suspend fun deleteGroupExpenseById(expenseId: Long): DeleteResult {
        val (responseCode, _) = requestSender.sendDelete("/group/expenses/$expenseId")
        return when (responseCode) {
            204 -> {
                // if successful, delete from localdb
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

    //region Add spending screen
    suspend fun getUserById(userId: Long): User? {
        return mainScope.async {
            val user: User?
            withContext(Dispatchers.IO) {
                user = db.userDao().getUserById(userId)
            }
            return@async user
        }.await()
    }

    suspend fun getUsersOfGroup(groupId: Long): List<SpendingDeptor> {
        return mainScope.async {
            val users: List<SpendingDeptor>
            withContext(Dispatchers.IO) {
                users = db.userDao().getGroupUsers(groupId)
            }
            return@async users
        }.await()
    }

    suspend fun addNewPersonalExpense(
        expense: NewPersonalSpending,
        image: ImageBitmap?,
        imageName: String?
    ) {
        // save expense to db
        mainScope.async {
            val personalExpense = Expense(
                0,
                null,
                true,
                expense.amount,
                expense.description,
                LocalDateTime.ofInstant(expense.date.toInstant(), ZoneId.of("UTC")),
                expense.category,
                settings.user_id,
                settings.user_id,
                settings.group_ip
            )
            var expenseId: Long
            withContext(Dispatchers.IO) {
                expenseId = db.expenseDao().saveExpense(personalExpense)
            }
            // save image to db if exists
            if (image != null) {
                val img = Image(expenseId, true, imageName ?: "", compressImage(image))
                db.imageDao().save(img)
            }
            return@async
        }.await()
    }

    suspend fun addNewGroupExpense(
        expense: NewGroupSpending,
        image: ImageBitmap?,
        imageName: String?
    ) {
        mainScope.async {
            // save expense to db
            val groupExpense = Expense(
                0,
                null,
                true,
                expense.amount,
                expense.description,
                LocalDateTime.ofInstant(expense.date.toInstant(), ZoneId.of("UTC")),
                expense.category,
                expense.initiatorUserId,
                settings.user_id,
                expense.groupId
            )
            var expenseId: Long
            withContext(Dispatchers.IO) {
                expenseId = db.expenseDao().saveExpense(groupExpense)
            }
            // save all participants
            val users = expense.users.map { user ->
                ExpenseUser(
                    0L,
                    expense.amount.divide(BigDecimal.valueOf(user.multiplier.toDouble())),
                    user.multiplier,
                    user.userId,
                    expenseId
                )
            }
            withContext(Dispatchers.IO) {
                users.forEach { user -> db.expenseUserDao().saveExpenseUser(user) }
            }
            // save image to db if exists
            if (image != null) {
                val img = Image(expenseId, true, imageName ?: "", compressImage(image))
                db.imageDao().save(img)
            }
            return@async
        }.await()
    }
}
//endregion

//region Group screen
data class BarChartData(
    var id: Long,
    var data: Pair<String, Double>
)
//endregion

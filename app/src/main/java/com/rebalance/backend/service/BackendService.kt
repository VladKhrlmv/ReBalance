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
import com.rebalance.backend.api.dto.request.ApiGroupAddUserRequest
import com.rebalance.backend.api.dto.request.ApiGroupCreateRequest
import com.rebalance.backend.api.dto.request.ApiLoginRequest
import com.rebalance.backend.api.dto.request.ApiRegisterRequest
import com.rebalance.backend.dto.*
import com.rebalance.backend.localdb.db.AppDatabase
import com.rebalance.backend.localdb.entities.*
import com.rebalance.service.notification.NotificationService
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class BackendService {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val mainScope = CoroutineScope(Dispatchers.Main)

    private lateinit var db: AppDatabase
    private lateinit var settings: Settings
    private lateinit var requestSender: RequestSender
    private lateinit var notificationService: NotificationService

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
            notificationService = NotificationService(context)
            onInit()
        }
    }

    //region updating settings in db
    private suspend fun updateUserInSettings(
        userId: Long,
        personalGroupId: Long,
        token: String,
        currency: String
    ) {
        this.settings.user_id = userId
        this.settings.group_id = personalGroupId
        this.settings.token = token
        this.settings.currency = currency
        mainScope.async {
            withContext(Dispatchers.IO) {
                db.settingsDao().saveSettings(settings)
            }
            return@async
        }.await()
        requestSender = RequestSender(settings.server_ip, settings.token)
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

    private suspend fun updateLastUpdateDate(newDate: LocalDateTime) {
        this.settings.lastUpdateDate = newDate
        return mainScope.async {
            withContext(Dispatchers.IO) {
                db.settingsDao().saveSettings(settings)
            }
            return@async
        }.await()
    }
    //endregion

    //region settings
    fun getUserId(): Long {
        return settings.user_id
    }

    fun getGroupId(): Long {
        return settings.group_id
    }

    fun getPersonalCurrency(): String {
        return settings.currency
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
            val userGroup = UserGroup(0, BigDecimal.ZERO, userId, personalGroupId)
            withContext(Dispatchers.IO) {
                db.userGroupDao().saveUserGroup(userGroup)
            }
            return@async
        }.await()
    }

    private suspend fun fetchUserData(): Boolean {
        var result = true
        mainScope.async {
            // fetch and save all groups (where user is in)
            val (responseCodeUserGroups, responseBodyUserGroups) = requestSender.sendGet("/user/groups")
            if (responseCodeUserGroups != 200) {
                result = false
                return@async
            }
            val userGroups = RequestParser.responseToGroupList(responseBodyUserGroups)
            withContext(Dispatchers.IO) {
                userGroups.forEach { g ->
                    db.groupDao().saveGroup(Group(g.id, false, g.name, g.currency, false))
                }
            }
            // fetch and save all users for each group and their relations with group
            for (group in userGroups) {
                val (responseCodeUsers, responseBodyUsers) = requestSender.sendGet("/group/${group.id}/users")
                if (responseCodeUsers != 200) {
                    result = false
                    return@async
                }
                val users = RequestParser.responseToUserList(responseBodyUsers)
                withContext(Dispatchers.IO) {
                    users.forEach { u ->
                        db.userDao().save(User(u.id, u.nickname, u.email))
                        db.userGroupDao().saveUserGroup(UserGroup(0L, u.balance, u.id, group.id))
                    }
                }
                // fetch and save all group expenses for a group
                var currentPage = 0
                while (true) {
                    // iterate over pages with size 20 of expenses
                    val (responseCodeGroupExpenses, responseBodyGroupExpenses) = requestSender.sendGet(
                        "/group/${group.id}/expenses?size=20&page=$currentPage"
                    )
                    if (responseCodeGroupExpenses != 200) {
                        result = false
                        return@async
                    }
                    val groupExpenses =
                        RequestParser.responseToGroupExpenseList(responseBodyGroupExpenses)
                    withContext(Dispatchers.IO) {
                        // save each group expense in page to localdb
                        for (ge in groupExpenses.content) {
                            val expenseId = db.expenseDao().saveExpense(
                                Expense(
                                    0L,
                                    ge.id,
                                    false,
                                    ge.amount,
                                    ge.description,
                                    LocalDateTime.ofInstant(ge.date.toInstant(), ZoneId.of("UTC")),
                                    ge.category,
                                    ge.initiatorUserId,
                                    ge.addedByUserId,
                                    group.id
                                )
                            )
                            // save participants of group expense to localdb
                            for (eu in ge.users) {
                                db.expenseUserDao().saveExpenseUser(
                                    ExpenseUser(
                                        0L,
                                        eu.amount,
                                        eu.multiplier,
                                        eu.userId,
                                        expenseId
                                    )
                                )
                            }
                        }
                    }
                    currentPage++
                    if (currentPage == groupExpenses.totalPages || groupExpenses.totalPages == 0) {
                        break
                    }
                }
            }
            // fetch and save all personal expenses
            var currentPage = 0
            while (true) {
                // iterate over pages with size 20 of expenses
                val (responseCodePersonalExpenses, responseBodyPersonalExpenses) = requestSender.sendGet(
                    "/personal/expenses?size=20&page=$currentPage"
                )
                if (responseCodePersonalExpenses != 200) {
                    result = false
                    return@async
                }
                val personalExpenses =
                    RequestParser.responseToPersonalExpenseList(responseBodyPersonalExpenses)
                withContext(Dispatchers.IO) {
                    // save each personal expense in page to localdb
                    for (pe in personalExpenses.content) {
                        db.expenseDao().saveExpense(
                            Expense(
                                0L,
                                pe.id,
                                false,
                                pe.amount,
                                pe.description,
                                LocalDateTime.ofInstant(pe.date.toInstant(), ZoneId.of("UTC")),
                                pe.category,
                                settings.user_id,
                                settings.user_id,
                                settings.group_id
                            )
                        )
                    }
                }
                currentPage++
                if (currentPage == personalExpenses.totalPages || personalExpenses.totalPages == 0) {
                    break
                }
            }
            return@async
        }.await()
        // update last update time in db
        updateLastUpdateDate(LocalDateTime.now())
        return result
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
                updateUserInSettings(user.id, user.personalGroupId, token, user.currency)

                // update current user in db
                updateCurrentUser(
                    user.id,
                    user.nickname,
                    user.email,
                    user.personalGroupId,
                    user.currency
                )
                if (!fetchUserData()) {
                    LoginResult.ErrorOnDataFetch
                }
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
                    user.token,
                    user.currency
                )

                // update user in db
                updateCurrentUser(
                    user.id,
                    user.nickname,
                    user.email,
                    user.personalGroupId,
                    user.currency
                )

                // update last update time in db
                updateLastUpdateDate(LocalDateTime.now())

                RegisterResult.Registered
            }
            400 -> RegisterResult.IncorrectData
            409 -> RegisterResult.EmailAlreadyTaken
            else -> RegisterResult.ServerError
        }
    }

    suspend fun logout(): Boolean {
        val (_, _) = requestSender.sendPost("/user/logout", "")
        // update settings in db
        val defaultSettings = Settings.getDefaultInstance()
        updateUserInSettings(
            defaultSettings.user_id,
            defaultSettings.group_id,
            defaultSettings.token,
            defaultSettings.currency
        )

        // delete all records from localdb
        withContext(Dispatchers.IO) {
            db.groupDao().dropAllGroups()
            db.userDao().dropAllUsers()
        }
        return true
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
                    var dates: List<String>
                    withContext(Dispatchers.IO) {
                        dates = db.expenseDao().getUniqueExpenseDays(settings.group_id)
                    }
                    return@async dates
                }.await()
                // add dates to return list
                dates.forEach { d ->
                    val startDate =
                        LocalDate.parse(d, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
                    val endDate = startDate.plusDays(1).minusSeconds(1)
                    list.add(
                        ScaledDateItem(
                            d.format(formatter),
                            startDate,
                            endDate
                        )
                    )
                }
            }
            "Week" -> {
                fun getStartOfWeek(year: Int, weekOfYear: Int): String {
                    val calendar: Calendar = Calendar.getInstance(Locale.getDefault())
                    calendar.clear()
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear)
                    calendar.firstDayOfWeek = Calendar.MONDAY
                    calendar.minimalDaysInFirstWeek = 4

                    // Set the calendar to the first day of the week (Monday)
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val startDate = calendar.time
                    return formatter.format(startDate)
                }


                // get unique years from db
                val dates = mainScope.async {
                    var dates: List<String>
                    withContext(Dispatchers.IO) {
                        dates = db.expenseDao().getUniqueExpenseWeeks(settings.group_id)
                    }
                    return@async dates
                }.await()
                // add dates to return list
                dates.forEach { d ->
                    val (year, weekOfYear) = d.split("-").map { it.toInt() }
                    val startDateStr = getStartOfWeek(year, weekOfYear)
                    val startDate =
                        LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            .atStartOfDay()
                    val endDate = startDate.plusWeeks(1).minusSeconds(1)
                    list.add(
                        ScaledDateItem(
                            startDateStr + "\n" + endDate.format(formatter),
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
                        dates = db.expenseDao().getUniqueExpenseMonths(settings.group_id)
                    }
                    return@async dates
                }.await()
                // add dates to return list
                dates.forEach { d ->
                    val startDate =
                        LocalDate.parse(d, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
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
                        dates = db.expenseDao().getUniqueExpenseYears(settings.group_id)
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
                    settings.group_id,
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
                    settings.group_id,
                    category,
                    scaledDateItem.dateFrom,
                    scaledDateItem.dateTo
                )
            }
            sums.map {
                it.date =
                    it.date.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime()
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
            expenses.map {
                it.date =
                    it.date.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime()
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

    suspend fun addUserToGroup(groupId: Long, email: String): AddUserToGroupResult {
        val request = ApiGroupAddUserRequest(groupId, email)
        val (responseCode, responseBody) = requestSender.sendPost("/group/users", request.toJson())
        return when (responseCode) {
            200 -> {
                val response = RequestParser.responseToUserGroup(responseBody)
                val (responseUserCode, responseUserBody) = requestSender.sendGet("/user/email/$email")
                val userResponse = RequestParser.responseToUser(responseUserBody)
                if (responseUserCode == 200) {
                    // if successful adding user to group and getting user, add UserGroup and User to db
                    mainScope.async {
                        withContext(Dispatchers.IO) {
                            db.userDao().save(
                                User(
                                    userResponse.id,
                                    userResponse.nickname,
                                    userResponse.email
                                )
                            )
                        }
                        withContext(Dispatchers.IO) {
                            db.userGroupDao().saveUserGroup(
                                UserGroup(
                                    0L,
                                    BigDecimal.ZERO,
                                    response.userId,
                                    response.groupId
                                )
                            )
                        }
                        return@async
                    }.await()
                } else {
                    AddUserToGroupResult.CannotSaveUser
                }
                AddUserToGroupResult.Added
            }
            404 -> AddUserToGroupResult.UserNotFound
            409 -> AddUserToGroupResult.UserInGroup
            else -> AddUserToGroupResult.ServerError
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
                settings.group_id
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
            var totalMultipliers = 0
            expense.users.forEach { u -> totalMultipliers += u.multiplier }
            val users = expense.users.map { user ->
                ExpenseUser(
                    0L,
                    expense.amount.multiply(BigDecimal.valueOf(user.multiplier.toDouble() / totalMultipliers)),
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
    //endregion

    //region images
    suspend fun getImageByExpenseId(expenseId: Long): ImageBitmap? {
        return mainScope.async {
            // get image from db
            val image: Image?
            withContext(Dispatchers.IO) {
                image = db.imageDao().getByExpenseId(expenseId)
            }
            // return image if exists
            if (image == null) {
                return@async null
            } else {
                val imageData = Base64.decode(image.data, Base64.DEFAULT)
                return@async BitmapFactory.decodeByteArray(
                    imageData,
                    0,
                    imageData.size
                ).asImageBitmap()
            }
        }.await()
    }

    private fun compressImage(image: ImageBitmap): String {
        val maxWidth = 1920
        val maxHeight = 1080

        // Calculate the scaling factor
        val scaleFactor = maxOf(
            image.width.toFloat() / maxWidth,
            image.height.toFloat() / maxHeight,
            1f
        )

        // Scale the bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(
            image.asAndroidBitmap(),
            (image.width / scaleFactor).toInt(),
            (image.height / scaleFactor).toInt(),
            true
        )

        // Compress the bitmap to JPEG
        val quality = 50
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // Convert to Base64
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
    //endregion
}

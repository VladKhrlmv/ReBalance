package com.rebalance.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rebalance.backend.localdb.dao.ExpenseDao
import com.rebalance.backend.localdb.db.AppDatabase
import com.rebalance.backend.localdb.entities.Expense
import com.rebalance.backend.localdb.entities.ExpenseUser
import com.rebalance.backend.localdb.entities.Group
import com.rebalance.backend.localdb.entities.User
import com.rebalance.backend.localdb.entities.UserGroup
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ExpenseDaoTest {
    private val userEmail = "test@user.com"
    private val categoryName = "Test category"
    private val secondUserEmail = "second@user.com"
    private lateinit var database: AppDatabase
    private lateinit var expenseDao: ExpenseDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        expenseDao = database.expenseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    private fun insertBasicData(): List<Long> {
        val user = User(id = null, username = "test_user", email = userEmail)
        val group = Group(id = null, name = "test_group", currency = "USD")
        val category = Category(id = null, name = categoryName)

        val userId = expenseDao.insertUser(user)
        val groupId = expenseDao.insertGroup(group)
        val categoryId = expenseDao.insertCategory(category)

        val userGroup = UserGroup(userId = userId, groupId = groupId)

        expenseDao.insertUserGroup(userGroup)

        return listOf(userId, groupId, categoryId)
    }

    @Test
    fun writeAndReadExpense() = runBlocking {
        // Given
        val ids = insertBasicData()
        val userId = ids[0]
        val groupId = ids[1]
        val categoryId = ids[2]

        val expense = Expense(id = null, description = "test_expense", amount = 200.0f, date = LocalDate.now(), addedBy = userId, groupId = groupId, categoryId = categoryId)

        // When
        val expenseId = expenseDao.insertExpense(expense)
        val expenseUser = ExpenseUser(userId = userId, expenseId = expenseId, amount = 50.0, multiplier = 1)
        expenseDao.insertExpenseUser(expenseUser)

        // Then
        val expenseList = expenseDao.getExpenses()
        assertEquals(1, expenseList.size)

        val expenseFirst = expenseList.first()
        assertEquals(expense.description, expenseFirst.description)
        assertEquals(expense.amount, expenseFirst.amount)
    }

    @Test
    fun readExpenseDetails() = runBlocking {
        // Given
        val ids = insertBasicData()
        val userId = ids[0]
        val groupId = ids[1]
        val categoryId = ids[2]

        val expense = Expense(id = null, description = "test_expense", amount = 200.0f, date = LocalDate.now(), addedBy = userId, groupId = groupId, categoryId = categoryId)
        val secondUser = User(id = null, username = "second_test_user", email = secondUserEmail)

        // When
        val secondUserId = expenseDao.insertUser(secondUser)

        val expenseId = expenseDao.insertExpense(expense)
        val expenseUser = ExpenseUser(userId = userId, expenseId = expenseId, amount = 50.0, multiplier = 1)
        val secondExpenseUser = ExpenseUser(userId = secondUserId, expenseId = expenseId, amount = 150.0, multiplier = 1)
        expenseDao.insertExpenseUser(expenseUser)
        expenseDao.insertExpenseUser(secondExpenseUser)

        // Then
        val details = expenseDao.getExpenseDetails()
        assertEquals(1, details.size)

        val expenseDetail = details.first()
        assertEquals(userEmail, expenseDetail.userWhoAdded.email)
        assertEquals(categoryName, expenseDetail.category.name)
        assertEquals(2, expenseDetail.details.size)

        val secondDetails = expenseDetail.details[1]
        assertEquals(secondUserId, secondDetails.userId)
        assertEquals(secondExpenseUser.amount, secondDetails.amount)
    }

    @Test
    fun readExpenseDetailsById() = runBlocking {
        // Given
        val ids = insertBasicData()
        val userId = ids[0]
        val groupId = ids[1]
        val categoryId = ids[2]

        val expense = Expense(id = null, description = "test_expense", amount = 200.0f, date = LocalDate.now(), addedBy = userId, groupId = groupId, categoryId = categoryId)
        val secondUser = User(id = null, username = "second_test_user", email = secondUserEmail)

        // When
        val secondUserId = expenseDao.insertUser(secondUser)

        val expenseId = expenseDao.insertExpense(expense)
        val expenseUser = ExpenseUser(userId = userId, expenseId = expenseId, amount = 50.0, multiplier = 1)
        val secondExpenseUser = ExpenseUser(userId = secondUserId, expenseId = expenseId, amount = 150.0, multiplier = 1)
        expenseDao.insertExpenseUser(expenseUser)
        expenseDao.insertExpenseUser(secondExpenseUser)

        // Then
        val details = expenseDao.getExpenseDetailById(expenseId)
        assertNotNull(details)
        details!!

        assertEquals(userEmail, details.userWhoAdded.email)
        assertEquals(categoryName, details.category.name)
        assertEquals(2, details.details.size)

        val secondDetails = details.details[1]
        assertEquals(secondUserId, secondDetails.userId)
        assertEquals(secondExpenseUser.amount, secondDetails.amount)
    }
}

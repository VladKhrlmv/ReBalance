package com.rebalance.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.rebalance.local.dto.ExpenseDetail
import com.rebalance.local.entities.Category
import com.rebalance.local.entities.Expense
import com.rebalance.local.entities.ExpenseUser
import com.rebalance.local.entities.Group
import com.rebalance.local.entities.User
import com.rebalance.local.entities.UserGroup

@Dao
interface ExpenseDao {

    @Transaction
    @Query("SELECT * FROM Expense")
    fun getExpenses(): List<Expense>

    @Transaction
    @Query("SELECT * FROM Expense")
    fun getExpenseDetails(): List<ExpenseDetail>

    @Transaction
    @Query("SELECT * FROM Expense WHERE id = :expenseId")
    fun getExpenseDetailById(expenseId: Long): ExpenseDetail?

    @Insert
    fun insertUser(user: User): Long

    @Insert
    fun insertGroup(group: Group): Long

    @Insert
    fun insertUserGroup(userGroup: UserGroup)

    @Insert
    fun insertCategory(category: Category): Long

    @Insert
    fun insertExpense(expense: Expense): Long

    @Insert
    fun insertExpenseUser(expenseUser: ExpenseUser)

}
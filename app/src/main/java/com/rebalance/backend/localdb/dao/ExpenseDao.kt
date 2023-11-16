package com.rebalance.backend.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.rebalance.backend.localdb.dto.ExpenseDetail
import com.rebalance.backend.localdb.entities.Category
import com.rebalance.backend.localdb.entities.Expense
import com.rebalance.backend.localdb.entities.ExpenseUser
import com.rebalance.backend.localdb.entities.Group
import com.rebalance.backend.localdb.entities.User
import com.rebalance.backend.localdb.entities.UserGroup

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

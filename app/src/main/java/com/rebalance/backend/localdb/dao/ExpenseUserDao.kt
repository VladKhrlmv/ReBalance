package com.rebalance.backend.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rebalance.backend.dto.GroupExpenseItemUser
import com.rebalance.backend.localdb.entities.ExpenseUser

@Dao
interface ExpenseUserDao {
    @Query(
        "SELECT u.nickname AS user, -eu.amount AS amount " +
                "FROM expense_user eu " +
                "INNER JOIN users u ON eu.user_id = u.id " +
                "WHERE eu.expense_id = :expenseId"
    )
    suspend fun getGroupExpenseDeptors(expenseId: Long): List<GroupExpenseItemUser>

    @Insert
    suspend fun saveExpenseUser(expenseUser: ExpenseUser):Long
}

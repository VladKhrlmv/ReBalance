package com.rebalance.backend.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import com.rebalance.backend.dto.SumByCategoryItem
import com.rebalance.backend.localdb.entities.Expense
import java.time.LocalDateTime

@Dao
interface ExpenseDao {

    @Query("SELECT DISTINCT date FROM expenses WHERE group_id = :groupId")
    suspend fun getUniqueExpenseDays(groupId: Long): List<LocalDateTime>

    @Query("SELECT DISTINCT strftime('%Y-%W', date) FROM expenses WHERE group_id = :groupId")
    suspend fun getUniqueExpenseWeeks(groupId: Long): List<String>

    @Query("SELECT DISTINCT strftime('%Y-%m', date) FROM expenses WHERE group_id = :groupId")
    suspend fun getUniqueExpenseMonths(groupId: Long): List<String>

    @Query("SELECT DISTINCT strftime('%Y', date) FROM expenses WHERE group_id = :groupId")
    suspend fun getUniqueExpenseYears(groupId: Long): List<Int>

    @Query(
        "SELECT category, SUM(amount) AS amount " +
                "FROM expenses " +
                "WHERE group_id = :groupId " +
                "AND date >= :dateFrom " +
                "AND date <= :dateTo " +
                "GROUP BY category"
    )
    suspend fun getSumsByCategories(
        groupId: Long,
        dateFrom: LocalDateTime,
        dateTo: LocalDateTime
    ): List<SumByCategoryItem>

    @Query(
        "SELECT * FROM expenses " +
                "WHERE group_id = :groupId " +
                "AND category = :category " +
                "AND date >= :dateFrom " +
                "AND date <= :dateTo"
    )
    suspend fun getExpensesByCategory(
        groupId: Long,
        category: String,
        dateFrom: LocalDateTime,
        dateTo: LocalDateTime
    ): List<Expense>

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteById(expenseId: Long)
}

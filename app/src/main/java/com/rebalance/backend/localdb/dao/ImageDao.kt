package com.rebalance.backend.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rebalance.backend.localdb.entities.Image

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(image: Image)

    @Query("SELECT * FROM images WHERE expense_id = :expenseId")
    suspend fun getByExpenseId(expenseId: Long): Image?
}

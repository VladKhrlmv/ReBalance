package com.rebalance.backend.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rebalance.backend.dto.SpendingDeptor
import com.rebalance.backend.localdb.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?

    @Query(
        "SELECT u.id as userId, u.nickname, false as selected, 1 as multiplier " +
                " FROM users u " +
                "INNER JOIN user_group ug ON u.id = ug.user_id " +
                "WHERE ug.group_id = :groupId"
    )
    suspend fun getGroupUsers(groupId: Long): List<SpendingDeptor>

    @Query("DELETE FROM users")
    suspend fun dropAllUsers()
}

package com.rebalance.backend.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rebalance.backend.dto.BarChartItem
import com.rebalance.backend.localdb.entities.UserGroup

@Dao
interface UserGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserGroup(userGroup: UserGroup)

    @Query(
        "SELECT u.nickname AS username, ug.balance AS balance " +
                "FROM user_group ug " +
                "INNER JOIN users u ON ug.user_id = u.id " +
                "WHERE ug.group_id = :groupId"
    )
    suspend fun getUserBalancesForGroup(groupId: Long): List<BarChartItem>

    @Query("SELECT * FROM user_group WHERE group_id = :groupId AND user_id IN (:userIds)")
    suspend fun getUsersByIds(groupId: Long, userIds: Set<Long>): List<UserGroup>
}

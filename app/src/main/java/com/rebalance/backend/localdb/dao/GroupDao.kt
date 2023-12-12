package com.rebalance.backend.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rebalance.backend.localdb.entities.Group

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Long): Group?

    @Query(
        "SELECT g.* FROM groups g " +
                "INNER JOIN user_group ug ON g.id = ug.group_id " +
                "WHERE ug.user_id = :userId " +
                "AND g.personal = 0"
    )
    suspend fun getUserGroups(userId: Long): List<Group>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGroup(group: Group)

    @Query("DELETE FROM groups")
    suspend fun dropAllGroups()
}

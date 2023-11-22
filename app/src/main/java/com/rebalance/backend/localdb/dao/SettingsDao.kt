package com.rebalance.backend.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rebalance.backend.localdb.entities.*

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings LIMIT 1")
    fun getSettings(): Settings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSettings(settings: Settings)
}

package com.rebalance.backend.localdb.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rebalance.backend.localdb.converter.LocalDateTimeConverter
import com.rebalance.backend.localdb.dao.ExpenseDao
import com.rebalance.backend.localdb.dao.SettingsDao
import com.rebalance.backend.localdb.entities.*

@TypeConverters(LocalDateTimeConverter::class)
@Database(
    entities = [User::class, Group::class, UserGroup::class, Expense::class, ExpenseUser::class, Settings::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rebalance_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

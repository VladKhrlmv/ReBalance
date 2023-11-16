package com.rebalance.backend.localdb.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rebalance.backend.localdb.dao.ExpenseDao
import com.rebalance.backend.localdb.entities.Category
import com.rebalance.backend.localdb.entities.Expense
import com.rebalance.backend.localdb.entities.ExpenseUser
import com.rebalance.backend.localdb.entities.Group
import com.rebalance.backend.localdb.entities.User
import com.rebalance.backend.localdb.entities.UserGroup
import com.rebalance.util.converter.DateConverter

@TypeConverters(DateConverter::class)
@Database(
    entities = [User::class, Group::class, UserGroup::class, Category::class, Expense::class, ExpenseUser::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}

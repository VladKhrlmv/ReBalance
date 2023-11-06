package com.rebalance.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rebalance.local.dao.ExpenseDao
import com.rebalance.local.entities.Category
import com.rebalance.local.entities.Expense
import com.rebalance.local.entities.ExpenseUser
import com.rebalance.local.entities.Group
import com.rebalance.local.entities.User
import com.rebalance.local.entities.UserGroup
import com.rebalance.util.converter.DateConverter

@TypeConverters(DateConverter::class)
@Database(
    entities = [User::class, Group::class, UserGroup::class, Category::class, Expense::class, ExpenseUser::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
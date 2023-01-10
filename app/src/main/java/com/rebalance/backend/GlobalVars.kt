package com.rebalance.backend

import android.app.Application
import android.content.Context
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.ExpenseGroup

class GlobalVars : Application() {

    var user: ApplicationUser = ApplicationUser(1, "User1", "user.1@gmail.com")
//    var user: ApplicationUser = ApplicationUser(2, "User2", "user.2@gmail.com")

    var serverIp: String = "192.168.197.115:8080"

    var group: ExpenseGroup = ExpenseGroup(2, "Personal", "PLN")

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
    }

    fun getIp(): String {
        return serverIp
    }

    fun getPersonalGroup(): ExpenseGroup {
        return group
    }

}
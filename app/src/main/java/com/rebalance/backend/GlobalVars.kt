package com.rebalance.backend

import android.app.Application
import android.content.Context
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.ExpenseGroup

class GlobalVars : Application() {

    companion object {
        //    var user: ApplicationUser = ApplicationUser(1, "User1", "user.1@gmail.com")
        var user: ApplicationUser = ApplicationUser(2, "User2", "user.2@gmail.com")

        var serverIp: String = "192.168.1.16:8080"

        var group: ExpenseGroup = ExpenseGroup(3, "Personal", "PLN")

    }

    override fun onCreate() {
        super.onCreate()
    }

}
package com.rebalance.backend

import android.app.Application
import android.content.Context
import com.rebalance.backend.entities.ApplicationUser

class GlobalVars : Application() {

    var user: ApplicationUser = ApplicationUser(1, "User1", "user.1@gmail.com")

    var serverIp: String = "192.168.0.108:8080"

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

}
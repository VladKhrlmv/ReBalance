package com.rebalance.backend.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.LoginAndPassword
import com.rebalance.backend.exceptions.FailedLoginException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@RequiresApi(Build.VERSION_CODES.N)
fun login(toWhere: String, loginAndPassword: LoginAndPassword) : ApplicationUser {
    var res = ""
    val url = URL(toWhere)
    val requestBody = Gson().toJson(loginAndPassword)
    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "POST"
        doInput = true
        setRequestProperty("Content-Type", "application/json")
        val outputStreamWriter = OutputStreamWriter(outputStream)
        outputStreamWriter.write(requestBody)
        outputStreamWriter.flush()
        println("Sent 'POST' request to URL : $url, with body : $requestBody; Response Code : $responseCode")
        if(responseCode == 401){
            throw FailedLoginException("Invalid password for email: ${loginAndPassword.getEmail()}")
        }
        inputStream.bufferedReader().use {
            it.lines().forEach { line -> res = res.plus(line + "\n") }
        }
    }
    return jsonToApplicationUser(res)
}
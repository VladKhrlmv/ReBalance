package com.rebalance.backend.api

import android.os.Build
import android.os.StrictMode
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.LoginAndPassword
import com.rebalance.backend.exceptions.FailedLoginException
import com.rebalance.backend.exceptions.ServerException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@RequiresApi(Build.VERSION_CODES.N)
// TODO: change toWhere
fun login(toWhere: String, email: String, password: String) : ApplicationUser {
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    var res = ""
    val url = URL(toWhere)
    val requestBody = Gson().toJson(LoginAndPassword(email, password))
    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "POST"
        doInput = true
        setRequestProperty("Content-Type", "application/json")
        val outputStreamWriter = OutputStreamWriter(outputStream)
        outputStreamWriter.write(requestBody)
        outputStreamWriter.flush()
        println("Sent 'POST' request to URL : $url, with body : $requestBody; Response Code : $responseCode")
        if(responseCode == 401){
            throw FailedLoginException("Invalid password for email: $email")
        }
        inputStream.bufferedReader().use {
            it.lines().forEach { line -> res = res.plus(line + "\n") }
        }
    }
    return jsonToApplicationUser(res)
}

@RequiresApi(Build.VERSION_CODES.N)
fun register(toWhere: String, email: String, username: String) : LoginAndPassword {
    var res = ""
    val url = URL(toWhere)
    val requestBody = Gson().toJson(ApplicationUser(username, email))
    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "POST"
        doInput = true
        setRequestProperty("Content-Type", "application/json")
        val outputStreamWriter = OutputStreamWriter(outputStream)
        outputStreamWriter.write(requestBody)
        outputStreamWriter.flush()
        println("Sent 'POST' request to URL : $url, with body : $requestBody; Response Code : $responseCode")
        if(responseCode == 409 || responseCode == 400){
            throw ServerException(responseMessage)
        }
        inputStream.bufferedReader().use {
            it.lines().forEach { line -> res = res.plus(line + "\n") }
        }
    }
    return jsonToLoginAndPassword(res)
}
package com.rebalance.backend.api

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class RequestSender(
    private val baseUrl: String,
    var token: String
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    suspend fun sendGet(url: String): Pair<Int, String> =
        coroutineScope.async(Dispatchers.IO) {
            var respCode: Int
            var respBody = ""

            val request = URL(baseUrl + url)
            with(request.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                doInput = true
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Authorization", "Bearer $token")

                Log.d("net", "get to $url : $responseCode token $token")
                respCode = responseCode
                if (responseCode == 200) {
                    inputStream.bufferedReader().use {
                        respBody = it.readText()
                    }
                }
            }

            return@async Pair(respCode, respBody)
        }.await()

    suspend fun sendPost(url: String, body: String): Pair<Int, String> =
        coroutineScope.async(Dispatchers.IO) {
            var respCode: Int
            var respBody = ""

            val request = URL(baseUrl + url)
            with(request.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Authorization", "Bearer $token")
                val outputStreamWriter = OutputStreamWriter(outputStream)
                outputStreamWriter.write(body)
                outputStreamWriter.flush()

                Log.d("net", "post to $url : $responseCode token $token")
                respCode = responseCode
                if (responseCode == 200 || responseCode == 201) {
                    inputStream.bufferedReader().use {
                        respBody = it.readText()
                    }
                }
            }

            return@async Pair(respCode, respBody)
        }.await()

    suspend fun sendPut(url: String, body: String): Pair<Int, String> =
        coroutineScope.async(Dispatchers.IO) {
            var respCode: Int
            var respBody = ""

            val request = URL(baseUrl + url)
            with(request.openConnection() as HttpURLConnection) {
                requestMethod = "PUT"
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Authorization", "Bearer $token")
                val outputStreamWriter = OutputStreamWriter(outputStream)
                outputStreamWriter.write(body)
                outputStreamWriter.flush()

                Log.d("net", "put to $url : $responseCode token $token")
                respCode = responseCode
                if (responseCode == 200 || responseCode == 201) {
                    inputStream.bufferedReader().use {
                        respBody = it.readText()
                    }
                }
            }

            return@async Pair(respCode, respBody)
        }.await()

    suspend fun sendDelete(url: String): Pair<Int, String> =
        coroutineScope.async(Dispatchers.IO) {
            var respCode: Int

            val request = URL(baseUrl + url)
            with(request.openConnection() as HttpURLConnection) {
                requestMethod = "DELETE"
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Authorization", "Bearer $token")

                Log.d("net", "delete to $url : $responseCode token $token")
                respCode = responseCode
            }

            return@async Pair(respCode, "")
        }.await()
}

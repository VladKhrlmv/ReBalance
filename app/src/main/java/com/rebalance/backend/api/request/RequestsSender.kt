package com.rebalance.backend.api.request

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class RequestsSender(
    private val baseUrl: String
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    suspend fun sendGet(url: String): Pair<Int, String> =
        coroutineScope.async(Dispatchers.IO) {
            var respCode: Int
            var respBody: String

            val request = URL(baseUrl + url)
            with(request.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                doInput = true
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")

                respCode = responseCode
                inputStream.bufferedReader().use {
                    respBody = it.readText()
                }
            }

            return@async Pair(respCode, respBody)
        }.await()

    suspend fun sendPost(url: String, body: String): Pair<Int, String> =
        coroutineScope.async(Dispatchers.IO) {
            var respCode: Int
            var respBody: String

            val request = URL(baseUrl + url)
            with(request.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                val outputStreamWriter = OutputStreamWriter(outputStream)
                outputStreamWriter.write(body)
                outputStreamWriter.flush()

                respCode = responseCode
                inputStream.bufferedReader().use {
                    respBody = it.readText()
                }
            }

            return@async Pair(respCode, respBody)
        }.await()

    suspend fun sendPut(url: String, body: String): Pair<Int, String> =
        coroutineScope.async(Dispatchers.IO) {
            var respCode: Int
            var respBody: String

            val request = URL(baseUrl + url)
            with(request.openConnection() as HttpURLConnection) {
                requestMethod = "PUT"
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                val outputStreamWriter = OutputStreamWriter(outputStream)
                outputStreamWriter.write(body)
                outputStreamWriter.flush()

                respCode = responseCode
                inputStream.bufferedReader().use {
                    respBody = it.readText()
                }
            }

            return@async Pair(respCode, respBody)
        }.await()

    suspend fun sendDelete(url: String): Pair<Int, String> =
        coroutineScope.async(Dispatchers.IO) {
            var respCode: Int
            var respBody: String

            val request = URL(baseUrl + url)
            with(request.openConnection() as HttpURLConnection) {
                requestMethod = "DELETE"
                doInput = true
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")

                respCode = responseCode
                inputStream.bufferedReader().use {
                    respBody = it.readText()
                }
            }

            return@async Pair(respCode, respBody)
        }.await()
}

package com.rebalance.backend.api.request

import com.rebalance.backend.exceptions.ServerException
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

    companion object {
        fun sendGet(toWhere: String): String {
            var res = ""
            val url = URL(toWhere)
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                println("Sent 'GET' request to URL : $url; Response Code : $responseCode")
                if (responseCode == 409 || responseCode == 400 || responseCode == 204 || responseCode == 500 || responseCode == 404) {
                    throw ServerException()
                }
                inputStream.bufferedReader().use {
                    it.lines().forEach { line -> res = res.plus(line + "\n") }
                }
            }
            return res
        }

        fun sendPost(toWhere: String, requestBody: String): String {
            var res = ""
            val url = URL(toWhere)
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                val outputStreamWriter = OutputStreamWriter(outputStream)
                outputStreamWriter.write(requestBody)
                outputStreamWriter.flush()
                println("Sent 'POST' request to URL : $url, with body : $requestBody; Response Code : $responseCode")
                if (responseCode == 409 || responseCode == 400 || responseCode == 401) {
                    throw ServerException(responseMessage)
                }
                inputStream.bufferedReader().use {
                    it.lines().forEach { line -> res = res.plus(line + "\n") }
                }
            }
            return res
        }

        fun sendPut(toWhere: String, requestBody: String): String {
            var res = ""
            val url = URL(toWhere)
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "PUT"
                doInput = true
                setRequestProperty("Content-Type", "application/json")
                val outputStreamWriter = OutputStreamWriter(outputStream)
                outputStreamWriter.write(requestBody)
                outputStreamWriter.flush()
                println("Sent 'PUT' request to URL : $url, with body : $requestBody; Response Code : $responseCode")
                if (responseCode == 409 || responseCode == 400) {
                    throw ServerException(responseMessage)
                }
                inputStream.bufferedReader().use {
                    it.lines().forEach { line -> res = res.plus(line + "\n") }
                }
            }
            return res
        }

        fun sendDelete(toWhere: String): String {
            var res = ""
            val url = URL(toWhere)
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "DELETE"
                doInput = true
                setRequestProperty("Content-Type", "application/json")
                val outputStreamWriter = OutputStreamWriter(outputStream)
                outputStreamWriter.flush()
                println("Sent 'DELETE' request to URL : $url; Response Code : $responseCode")
                if (responseCode == 409 || responseCode == 400) {
                    throw ServerException(responseMessage)
                }
                inputStream.bufferedReader().use {
                    it.lines().forEach { line -> res = res.plus(line + "\n") }
                }
            }
            return res
        }
    }
}

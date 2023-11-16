package com.rebalance.backend.api

import com.google.gson.Gson
import com.rebalance.backend.api.request.RequestsSender.Companion.sendDelete
import com.rebalance.backend.api.request.RequestsSender.Companion.sendGet
import com.rebalance.backend.api.request.RequestsSender.Companion.sendPost
import com.rebalance.backend.api.entities.LoginAndPassword
import com.rebalance.backend.exceptions.ServerException
import org.junit.Assert.fail
import org.junit.Test

class RequestsSenderTest {
    private val ip = "http://192.168.180.253:8080/";

    @Test
    fun shouldGet() {
        try {
            sendGet(ip + "users/1")
        } catch (e: ServerException) {
            fail("should not fail")
        }
    }

    @Test
    fun shouldFailToGet() {
        try {
            sendGet(ip + "users/-1")
            fail("should fail")
        } catch (_: ServerException) {
        }
    }

    @Test
    fun shouldPost() {
        try {
            sendPost(
                ip + "users/login",
                Gson().toJson(LoginAndPassword("test@test.com", "test"))
            )
        } catch (e: ServerException) {
            fail("should not fail")
        }
    }

    @Test
    fun shouldFailToPost() {
        try {
            sendPost(
                ip + "users/login",
                Gson().toJson(LoginAndPassword("test@test.com", "testIncorrect"))
            )
            fail("should not fail")
        } catch (_: Exception) {
        }
    }

    @Test
    fun shouldFailToDelete() {
        try {
            sendDelete(ip + "expenses/-1")
            fail("should not fail")
        } catch (_: Exception) {
        }
    }
}

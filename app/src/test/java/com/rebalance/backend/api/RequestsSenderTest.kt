package com.rebalance.backend.api

import com.rebalance.backend.api.RequestsSender.Companion.sendGet
import com.rebalance.backend.api.RequestsSender.Companion.sendPost
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.exceptions.FailedLoginException
import com.rebalance.backend.exceptions.ServerException
import org.junit.Assert.*
import org.junit.Test

class RequestsSenderTest {
    @Test
    fun sendGetTest() {
        val expected = "{\"id\":1,\"username\":\"Aliaksei\",\"email\":\"user.1@gmail.com\"}\n"

        assertEquals(
            expected, sendGet("http://192.168.148.253:8080/users/1")
        )
    }

    @Test
    fun sendGetTestFail() {
        assertThrows(ServerException::class.java) {
            sendGet("http://192.168.148.253:8080/users/-1")
        }
    }
}

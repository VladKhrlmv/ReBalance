package com.rebalance.backend.api

import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.LoginAndPassword
import com.rebalance.backend.exceptions.FailedLoginException
import com.rebalance.backend.exceptions.ServerException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class LoginUtilsTest {
    @Test
    fun login() {
        val expected = ApplicationUser(1, "Aliaskei", "user.1@gmail.com")
        expected.setPassword("pass")

        assertEquals(
            expected, login(
                "http://192.168.148.253:8080/users/login",
                "user.1@gmail.com",
                "pass"
            )
        )
    }

    @Test
    fun loginFail() {
        val expected = ApplicationUser(1, "Aliaskei", "user.1@gmail.com")
        expected.setPassword("pass1")

        assertThrows(
            "Invalid password for email: user.1@gmail.com",
            FailedLoginException::class.java
        ) {
            login(
                "http://192.168.148.253:8080/users/login",
                "user.1@gmail.com",
                "pass1"
            )
        }
    }

    @Test
    fun register() {
        val expected = LoginAndPassword("user.1@gmail.com", "")

        assertEquals(
            expected, register(
                "http://192.168.148.253:8080/users/login",
                "user.1@gmail.com",
                "Aliaksei",
                "pass"
            )
        )
    }

    @Test
    fun registerFail() {
        val expected = ApplicationUser(1, "Aliaskei", "user.1@gmail.com")
        expected.setPassword("pass")

        assertThrows(ServerException::class.java) {
            register(
                "http://192.168.148.253:8080/users",
                "user.1@gmail.com",
                "Aliaksei",
                "pass"
            )
        }
    }
}

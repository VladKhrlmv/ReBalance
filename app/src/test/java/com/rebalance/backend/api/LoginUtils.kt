package com.rebalance.backend.api

import com.rebalance.backend.api.login
import com.rebalance.backend.entities.ApplicationUser
import com.rebalance.backend.entities.LoginAndPassword
import io.mockk.*
import org.junit.Test

import org.junit.Assert.*

class LoginUtilsTest {
    @Test
    fun login() {
        val expected = ApplicationUser(1, "Aliaskei", "user.1@gmail.com")
        expected.setPassword("pass")

        assertEquals(expected, login(
            "http://192.168.56.1:8080/users/login",
            "user.1@gmail.com",
            "pass"))
    }

    @Test
    fun register() {
        val expected = LoginAndPassword("user.1@gmail.com", "")

        assertEquals(expected, com.rebalance.backend.api.register(
            "http://192.168.56.1:8080/users/login",
            "user.1@gmail.com",
            "Aliaksei",
        "pass"))
    }
}

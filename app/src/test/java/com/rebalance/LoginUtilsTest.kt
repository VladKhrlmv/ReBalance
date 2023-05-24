package com.rebalance

import com.rebalance.backend.api.*
import org.junit.Test
import org.junit.Assert.*
import com.rebalance.backend.entities.ApplicationUser
import org.junit.After
import org.junit.Before

class LoginUtilsTest {

    @Before
    fun init() {
    }

    @After
    fun teardown() {
    }

    @Test
    fun login_isCorrect() {
        val expected = ApplicationUser(id = 1, "Aliaksei", "user.1@gmail.com")
        expected.setPassword("pass")
        assertEquals(expected, login("", "user.1@gmail.com", "pass"))
    }
}
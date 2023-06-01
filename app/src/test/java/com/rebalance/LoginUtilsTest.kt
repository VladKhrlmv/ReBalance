package com.rebalance

import com.rebalance.backend.api.login
import com.rebalance.backend.entities.ApplicationUser
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginUtilsTest {
    @Test
    fun login_isCorrect() {
        val expected = ApplicationUser(id = 1, "Aliaksei", "user.1@gmail.com")
        expected.setPassword("pass")
        assertEquals(expected, login("", "user.1@gmail.com", "pass"))
    }
}

package com.rebalance.backend.service

import com.rebalance.PreferencesData
import com.rebalance.backend.api.RequestsSender
import com.rebalance.backend.entities.Expense
import io.mockk.*
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

internal class BackendServiceTest {
    private val backendService = spyk(BackendService(PreferencesData("", "1", 1))) {
        every { setPolicy() } just runs
    }

    @Before
    fun init() {
        mockkObject(RequestsSender)
        MockKAnnotations.init(this)
    }

    @After
    fun teardown() {
        unmockkObject(RequestsSender)
    }

    @Test
    fun getScaleItems() {
        val expected = listOf(
            ScaleItem("Day", "D"),
            ScaleItem("Week", "W"),
            ScaleItem("Month", "M"),
            ScaleItem("Year", "Y")
        )

        assertEquals(expected, backendService.getScaleItems())
    }

    @Test
    fun getScaledDateItems() { //TODO
    }

    @Test
    fun getPersonal() { //TODO: add more tests with more expenses
        val expected = ArrayList<ExpenseItem>()
        expected.add(ExpenseItem(Expense(99, 100.0, "2023-01-16", "testtesttest", "testtesttest", -1)))

        every { RequestsSender.sendGet(any()) } returns "[{\"id\":99,\"amount\":100.0,\"description\":\"testtesttest\",\"dateStamp\":\"2023-01-16\",\"category\":\"testtesttest\",\"globalId\":-1,\"user\":{\"id\":5,\"username\":\"Random\",\"email\":\"user.5@gmail.com\"},\"group\":{\"id\":6,\"name\":\"peruser.5@gmail.com\",\"currency\":\"PLN\",\"users\":[{\"id\":5,\"username\":\"Random\",\"email\":\"user.5@gmail.com\"}]}}]"

        val actual = backendService.getPersonal(
            LocalDate.of(2023, 1, 16),
            LocalDate.of(2023, 1, 16))
        assertEquals(expected, actual)
    }

    @Test
    fun getGroups() {
    }

    @Test
    fun getGroupById() {
    }

    @Test
    fun getGroupVisualBarChart() {
    }

    @Test
    fun getGroupList() {
    }
}

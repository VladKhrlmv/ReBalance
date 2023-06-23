package com.rebalance

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.rebalance.ui.screen.authentication.AuthenticationScreen
import com.rebalance.ui.theme.ReBalanceTheme
import org.junit.Rule
import org.junit.Test

class Tests {
    @get:Rule
    val composeTestRule = createComposeRule()
    var id = 14

    @Test
    fun signInNavigation() {
        // Start the app
        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("SIGN UP").performClick()

        composeTestRule.onNodeWithText("SIGN IN").assertIsDisplayed()

        composeTestRule.onNodeWithText("Mail").performClick()

        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }

    // change email and username before run
    @Test
    fun signUpSuccessful() {
        // Start the app
        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("SIGN UP").performClick()
        composeTestRule.onNodeWithText("Mail").performClick()

        composeTestRule.onNodeWithText("E-mail").performTextInput("user_test_${id}@example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("User test ${id}")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")
        composeTestRule.onNodeWithText("Repeat password").performTextInput("pass")
        composeTestRule.onNodeWithText("Your currency").performTextInput("PLN")

        composeTestRule.onNodeWithText("SIGN UP").performClick()

        composeTestRule.onNodeWithText("Welcome to the ReBalance application").assertIsDisplayed()
    }

    @Test
    fun signInSuccessful() {
        // Start the app
        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("Login").performTextInput("user_test_${id}@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")

        composeTestRule.onNodeWithText("SIGN IN").performClick()

        composeTestRule.onNodeWithText("Personal").assertIsDisplayed()
    }

    @Test
    fun signInFailed() {
        // Start the app
        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("Login").performTextInput("no_such_user")
        composeTestRule.onNodeWithText("Password").performTextInput("no_such_password")

        composeTestRule.onNodeWithText("SIGN IN").performClick()

        composeTestRule.onNodeWithText("SIGN IN").assertIsDisplayed()
//        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")
    }

    @Test
    fun signUpFailed() {
        // Start the app
        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("SIGN UP").performClick()
        composeTestRule.onNodeWithText("Mail").performClick()

        composeTestRule.onNodeWithText("E-mail").performTextInput("user_test_${id}@example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("test_user_assertion")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")
        composeTestRule.onNodeWithText("Repeat password").performTextInput("pass")
        composeTestRule.onNodeWithText("Your currency").performTextInput("PLN")

        composeTestRule.onNodeWithText("SIGN UP").performClick()

        composeTestRule.onNodeWithText("SIGN UP").assertIsDisplayed()
    }

    @Test
    fun tutorialPassed() {
        id += 1

        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }
        composeTestRule.onNodeWithText("SIGN UP").performClick()
        composeTestRule.onNodeWithText("Mail").performClick()

        composeTestRule.onNodeWithText("E-mail").performTextInput("user_test_tutorial_${id}@example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("Test tutorial")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")
        composeTestRule.onNodeWithText("Repeat password").performTextInput("pass")
        composeTestRule.onNodeWithText("Your currency").performTextInput("PLN")

        composeTestRule.onNodeWithText("SIGN UP").performClick()

        while (true) {
            try {
                composeTestRule.onNode(hasText("Next")).assertExists()
                composeTestRule.onNodeWithText("Next").performClick()
            } catch (e: AssertionError) {
                break
            }
        }
        composeTestRule.onNodeWithText("End").performClick()

        composeTestRule.onNodeWithText("Personal").assertIsDisplayed()
    }

    @Test
    fun tutorialSkipped() {
        id += 1

        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }
        composeTestRule.onNodeWithText("SIGN UP").performClick()
        composeTestRule.onNodeWithText("Mail").performClick()

        composeTestRule.onNodeWithText("E-mail").performTextInput("user_test_tutorial_${id}@example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("Test tutorial")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")
        composeTestRule.onNodeWithText("Repeat password").performTextInput("pass")
        composeTestRule.onNodeWithText("Your currency").performTextInput("PLN")

        composeTestRule.onNodeWithText("SIGN UP").performClick()

        composeTestRule.onNodeWithText("Skip").performClick()

        composeTestRule.onNodeWithText("Personal").assertIsDisplayed()
    }

    @Test
    fun mainNavigation() {
        // Start the app
        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("Login").performTextInput("user_test_${id}@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")

        composeTestRule.onNodeWithText("SIGN IN").performClick()

        val viewSwitcher = composeTestRule.onNode(hasTestTag("viewSwitcher"), useUnmergedTree = true)

        viewSwitcher.performClick()

        composeTestRule.onNode(hasTestTag("personalList"), useUnmergedTree = true).assertExists()

        composeTestRule.onNodeWithText("Group").performClick()

        composeTestRule.onNodeWithText("List").performClick()

        composeTestRule.onNode(hasTestTag("groupList"), useUnmergedTree = true).assertExists()

        composeTestRule.onNodeWithContentDescription("Add spending").performClick()

        composeTestRule.onNodeWithText("Title").assertIsDisplayed()

        composeTestRule.onNode(hasTestTag("logout"), useUnmergedTree = true).performClick()

        composeTestRule.onNodeWithText("SIGN IN").assertIsDisplayed()
    }

    // change group name before run
    @Test
    fun createAndInviteToGroup() {
        // Start the app
        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("Login").performTextInput("user_test_${id}@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")
        composeTestRule.onNodeWithText("SIGN IN").performClick()

        composeTestRule.onNodeWithText("Group").performClick()
        composeTestRule.onNodeWithText("Create").performClick()
        composeTestRule.onNodeWithText("Name").performTextInput("assertion_test_group${id}")
        composeTestRule.onNodeWithText("Currency").performTextInput("PLN")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("Email").performTextInput("user_test_${id - 1}@example.com")
        composeTestRule.onNodeWithText("Invite").performClick()

        composeTestRule.onNode(hasTestTag("groupBarChart"), useUnmergedTree = true).assertExists()

    }

    // change title before run
    @Test
    fun addPersonalSpending() {
        // Start the app
        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("Login").performTextInput("user_test_${id}@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")

        composeTestRule.onNodeWithText("SIGN IN").performClick()



        val viewSwitcher = composeTestRule.onNode(hasTestTag("viewSwitcher"), useUnmergedTree = true)

        composeTestRule.onNodeWithContentDescription("Add spending").performClick()

        composeTestRule.onNodeWithText("Title").performTextInput("Assertion_tests${id}")
        composeTestRule.onNodeWithText("Category").performTextInput("Assertion_tests${id}")
        composeTestRule.onNode(hasTestTag("addCost"), useUnmergedTree = true).performTextInput("2.2")
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Group").performClick()
        composeTestRule.onNodeWithText("Personal").performClick()

        viewSwitcher.performClick()

        composeTestRule.onNodeWithText("Assertion_tests${id}").assertIsDisplayed()
    }

    // change title before run
    @Test
    fun addGroupSpending() {
        // Start the app
        composeTestRule.setContent {
            ReBalanceTheme {
                AuthenticationScreen(rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("Login").performTextInput("user_test_${id}@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")
        composeTestRule.onNodeWithText("SIGN IN").performClick()


        composeTestRule.onNodeWithContentDescription("Add spending").performClick()

        composeTestRule.onNodeWithText("Title").performTextInput("Assertion_group_tests${id}")
        composeTestRule.onNodeWithText("Category").performTextInput("Assertion_group_tests${id}")
        composeTestRule.onNode(hasTestTag("addCost"), useUnmergedTree = true).performTextInput("2.2")
        composeTestRule.onNode(hasTestTag("groupExpenseCheckBox"), useUnmergedTree = true).performClick()
        composeTestRule.onNode(hasTestTag("groupSelectExpenseDropdown"), useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("assertion_test_group${id}").performClick()
        composeTestRule.onNodeWithText("User test ${id}").performClick()

        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Personal").performClick()
        composeTestRule.onNodeWithText("Group").performClick()
        composeTestRule.onNodeWithText("List").performClick()
        composeTestRule.onNode(hasTestTag("groupSelectionGroupScreen"), useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("assertion_test_group${id}").performClick()

        composeTestRule.onNodeWithText("Assertion_group_tests${id}").assertIsDisplayed()
    }
}

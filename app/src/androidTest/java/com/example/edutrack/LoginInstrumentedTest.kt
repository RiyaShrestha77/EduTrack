package com.example.edutrack

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.edutrack.view.LoginActivity
import com.example.edutrack.view.RegistrationActivity
import com.example.edutrack.view.DashboardActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testNavigationToRegistration() {

        composeRule.onNodeWithTag("register_link")
            .performClick()


        Intents.intended(hasComponent(RegistrationActivity::class.java.name))
    }

    @Test
    fun testSuccessfulLogin_navigatesToDashboard() {

        composeRule.onNodeWithTag("email")
            .performTextInput("test@gmail.com")


        composeRule.onNodeWithTag("password")
            .performTextInput("123456")


        composeRule.onNodeWithTag("login_button")
            .performClick()
    }
}

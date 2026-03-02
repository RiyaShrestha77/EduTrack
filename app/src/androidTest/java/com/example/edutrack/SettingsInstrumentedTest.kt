package com.example.edutrack

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.edutrack.view.DashboardActivity
import com.example.edutrack.view.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsInstrumentedTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<DashboardActivity>()

    @Before
    fun setup() {
        Intents.init()

        composeRule.onNodeWithTag("nav_settings").performClick()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testLogout_navigatesToLoginActivity() {

        composeRule.onNodeWithTag("logout_card")
            .performClick()


        Intents.intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun testPrivacySecurity_showsPolicy() {

        composeRule.onNodeWithTag("setting_Privacy & Security")
            .performClick()


        composeRule.onNodeWithTag("setting_Privacy & Security").assertDoesNotExist()
    }
}

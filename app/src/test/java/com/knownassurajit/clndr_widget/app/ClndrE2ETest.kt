package com.knownassurajit.clndr_widget.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class ClndrE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppOnboardingAndNavigation() {
        ShadowLog.stream = System.out

        // 1. Verify Onboarding screen is shown when birthdate is null (clean state)
        composeTestRule.onNodeWithText("C L N D R").assertExists()
        composeTestRule.onNodeWithText("Date of birth").assertExists()

        // 2. Select Date of Birth via picker
        composeTestRule.onNodeWithText("Tap to choose").performClick()
        composeTestRule.waitForIdle()

        // Click Set on the DatePickerDialog stub
        composeTestRule.onNodeWithText("Set").performClick()
        composeTestRule.waitForIdle()

        // 3. Confirm onboarding
        composeTestRule.onNodeWithText("See my life").performClick()

        // Loop and idle the main looper to allow background writes and main thread resumptions to execute
        repeat(10) {
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            Thread.sleep(100)
        }
        composeTestRule.waitForIdle()

        // 4. Verify main screen is shown after onboarding
        composeTestRule.onNodeWithText("In Progress").assertExists()

        // 5. Navigate to Milestones tab
        composeTestRule.onNodeWithText("Goals").performClick()
        composeTestRule.waitForIdle()

        // Verify Milestones screen header / empty state
        composeTestRule.onNodeWithText("No milestones yet.\nAnchor one to start counting.").assertExists()
    }
}


package com.example.emojifun

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun typingKeywordShowsMatchingEmojiAndSuggestions() {
        composeTestRule.onNode(hasSetTextAction()).performTextInput("party")

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("🥳 Partying Face").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("🥳 Partying Face").assertIsDisplayed()
        composeTestRule.onNodeWithText("😀🥳🎉 · Big celebration").assertIsDisplayed()
    }
}
 


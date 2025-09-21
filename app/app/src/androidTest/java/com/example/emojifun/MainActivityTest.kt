 (cd "$(git rev-parse --show-toplevel)" && git apply --3way <<'EOF' 
diff --git a//dev/null b/app/src/androidTest/java/com/example/emojifun/MainActivityTest.kt
index 0000000000000000000000000000000000000000..47a0006292a07e6776aee9e03c370e9b8f811bc9 100644
--- a//dev/null
+++ b/app/src/androidTest/java/com/example/emojifun/MainActivityTest.kt
@@ -0,0 +1,31 @@
+package com.example.emojifun
+
+import androidx.compose.ui.test.assertIsDisplayed
+import androidx.compose.ui.test.hasSetTextAction
+import androidx.compose.ui.test.junit4.createAndroidComposeRule
+import androidx.compose.ui.test.onAllNodesWithText
+import androidx.compose.ui.test.onNodeWithText
+import androidx.compose.ui.test.performTextInput
+import androidx.test.ext.junit.runners.AndroidJUnit4
+import org.junit.Rule
+import org.junit.Test
+import org.junit.runner.RunWith
+
+@RunWith(AndroidJUnit4::class)
+class MainActivityTest {
+
+    @get:Rule
+    val composeTestRule = createAndroidComposeRule<MainActivity>()
+
+    @Test
+    fun typingKeywordShowsMatchingEmojiAndSuggestions() {
+        composeTestRule.onNode(hasSetTextAction()).performTextInput("party")
+
+        composeTestRule.waitUntil(timeoutMillis = 5_000) {
+            composeTestRule.onAllNodesWithText("🥳 Partying Face").fetchSemanticsNodes().isNotEmpty()
+        }
+
+        composeTestRule.onNodeWithText("🥳 Partying Face").assertIsDisplayed()
+        composeTestRule.onNodeWithText("😀🥳🎉 · Big celebration").assertIsDisplayed()
+    }
+}
 
EOF
)

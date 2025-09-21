 (cd "$(git rev-parse --show-toplevel)" && git apply --3way <<'EOF' 
diff --git a//dev/null b/app/src/test/java/com/example/emojifun/EmojiRepositoryTest.kt
index 0000000000000000000000000000000000000000..51db877f964bd346f324c073d0446e4d730a5653 100644
--- a//dev/null
+++ b/app/src/test/java/com/example/emojifun/EmojiRepositoryTest.kt
@@ -0,0 +1,56 @@
+package com.example.emojifun
+
+import kotlinx.coroutines.test.runTest
+import org.junit.Assert.assertEquals
+import org.junit.Assert.assertTrue
+import org.junit.Test
+
+class EmojiRepositoryTest {
+
+    private val sampleDefinitions = listOf(
+        EmojiDefinition(
+            char = "😀",
+            name = "Grinning Face",
+            keywords = listOf("smile", "happy", "party"),
+            suggestions = listOf(
+                EmojiSuggestion(
+                    label = "Happy party",
+                    sequence = listOf("😀", "🥳"),
+                    keywords = listOf("party")
+                )
+            )
+        ),
+        EmojiDefinition(
+            char = "❤️",
+            name = "Red Heart",
+            keywords = listOf("love", "romance"),
+            suggestions = listOf(
+                EmojiSuggestion(
+                    label = "Romantic dinner",
+                    sequence = listOf("❤️", "🍷"),
+                    keywords = listOf("date")
+                )
+            )
+        )
+    )
+
+    private val repository = EmojiRepository(object : EmojiDefinitionProvider {
+        override suspend fun getEmojiDefinitions(): List<EmojiDefinition> = sampleDefinitions
+    })
+
+    @Test
+    fun searchEmojis_matchesKeywordAndReturnsSuggestions() = runTest {
+        val response = repository.searchEmojis("party")
+
+        assertTrue(response.results.any { it.name == "Grinning Face" })
+        assertTrue(response.suggestions.any { it.label.contains("Happy party") })
+    }
+
+    @Test
+    fun searchEmojis_returnsAllWhenQueryBlank() = runTest {
+        val response = repository.searchEmojis("")
+
+        assertEquals(sampleDefinitions.size, response.results.size)
+        assertTrue(response.suggestions.isNotEmpty())
+    }
+}
 
EOF
)

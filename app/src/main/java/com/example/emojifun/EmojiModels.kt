
+package com.example.emojifun
+
+data class EmojiDefinition(
+    val char: String,
+    val name: String,
+    val keywords: List<String>,
+    val suggestions: List<EmojiSuggestion>
+)
+
+data class EmojiSuggestion(
+    val label: String,
+    val sequence: List<String>,
+    val keywords: List<String>
+)
+
+data class EmojiSearchResult(
+    val emoji: EmojiDefinition,
+    val score: Int
+)
+
+data class EmojiSearchResponse(
+    val results: List<EmojiDefinition>,
+    val suggestions: List<EmojiSuggestion>
+)
 


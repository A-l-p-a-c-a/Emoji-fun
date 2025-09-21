
+package com.example.emojifun
+
+import android.content.Context
+import kotlinx.coroutines.Dispatchers
+import kotlinx.coroutines.withContext
+import org.json.JSONArray
+import org.json.JSONObject
+
+class EmojiLocalDataSource(private val context: Context) : EmojiDefinitionProvider {
+
+    @Volatile
+    private var cachedDefinitions: List<EmojiDefinition>? = null
+
+    override suspend fun getEmojiDefinitions(): List<EmojiDefinition> = withContext(Dispatchers.IO) {
+        cachedDefinitions ?: loadFromAssets().also { cachedDefinitions = it }
+    }
+
+    private fun loadFromAssets(): List<EmojiDefinition> {
+        val assetManager = context.assets
+        val json = assetManager.open("emojis.json").bufferedReader().use { it.readText() }
+        val root = JSONObject(json)
+        val emojiArray = root.optJSONArray("emojis") ?: JSONArray()
+        val definitions = mutableListOf<EmojiDefinition>()
+        for (i in 0 until emojiArray.length()) {
+            val obj = emojiArray.optJSONObject(i) ?: continue
+            val char = obj.optString("char")
+            val name = obj.optString("name")
+            val keywords = obj.optJSONArray("keywords")?.toStringList().orEmpty()
+            val suggestionArray = obj.optJSONArray("suggestions") ?: JSONArray()
+            val suggestions = mutableListOf<EmojiSuggestion>()
+            for (j in 0 until suggestionArray.length()) {
+                val suggestionObject = suggestionArray.optJSONObject(j) ?: continue
+                val label = suggestionObject.optString("label")
+                val sequence = suggestionObject.optJSONArray("sequence")?.toStringList().orEmpty()
+                val suggestionKeywords = suggestionObject.optJSONArray("keywords")?.toStringList().orEmpty()
+                suggestions.add(
+                    EmojiSuggestion(
+                        label = label,
+                        sequence = sequence,
+                        keywords = suggestionKeywords
+                    )
+                )
+            }
+            if (char.isNotBlank()) {
+                definitions.add(
+                    EmojiDefinition(
+                        char = char,
+                        name = name,
+                        keywords = keywords,
+                        suggestions = suggestions
+                    )
+                )
+            }
+        }
+        return definitions
+    }
+
+    private fun JSONArray.toStringList(): List<String> {
+        val list = mutableListOf<String>()
+        for (i in 0 until length()) {
+            list.add(optString(i))
+        }
+        return list
+    }
+}
 

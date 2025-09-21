
+package com.example.emojifun
+
+interface EmojiDefinitionProvider {
+    suspend fun getEmojiDefinitions(): List<EmojiDefinition>
+}

)

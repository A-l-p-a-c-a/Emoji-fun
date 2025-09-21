 (cd "$(git rev-parse --show-toplevel)" && git apply --3way <<'EOF' 
diff --git a//dev/null b/app/src/main/java/com/example/emojifun/EmojiDefinitionProvider.kt
index 0000000000000000000000000000000000000000..53c29417c3b1afcefd2ad8b99797dac400686753 100644
--- a//dev/null
+++ b/app/src/main/java/com/example/emojifun/EmojiDefinitionProvider.kt
@@ -0,0 +1,5 @@
+package com.example.emojifun
+
+interface EmojiDefinitionProvider {
+    suspend fun getEmojiDefinitions(): List<EmojiDefinition>
+}
 
EOF
)

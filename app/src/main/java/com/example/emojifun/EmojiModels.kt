 (cd "$(git rev-parse --show-toplevel)" && git apply --3way <<'EOF' 
diff --git a//dev/null b/app/src/main/java/com/example/emojifun/EmojiModels.kt
index 0000000000000000000000000000000000000000..f44b4c7f1ec8bf7db3d43b6a6480cc68ade04d11 100644
--- a//dev/null
+++ b/app/src/main/java/com/example/emojifun/EmojiModels.kt
@@ -0,0 +1,24 @@
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
 
EOF
)

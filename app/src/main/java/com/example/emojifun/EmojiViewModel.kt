 (cd "$(git rev-parse --show-toplevel)" && git apply --3way <<'EOF' 
diff --git a//dev/null b/app/src/main/java/com/example/emojifun/EmojiViewModel.kt
index 0000000000000000000000000000000000000000..37574c389efe60e8abb40d34f9449dacc2e152b7 100644
--- a//dev/null
+++ b/app/src/main/java/com/example/emojifun/EmojiViewModel.kt
@@ -0,0 +1,62 @@
+package com.example.emojifun
+
+import android.app.Application
+import androidx.lifecycle.AndroidViewModel
+import androidx.lifecycle.viewModelScope
+import kotlinx.coroutines.Job
+import kotlinx.coroutines.delay
+import kotlinx.coroutines.flow.MutableStateFlow
+import kotlinx.coroutines.flow.StateFlow
+import kotlinx.coroutines.flow.asStateFlow
+import kotlinx.coroutines.launch
+
+class EmojiViewModel(application: Application) : AndroidViewModel(application) {
+
+    private val repository = EmojiRepository(EmojiLocalDataSource(application))
+
+    private val _uiState = MutableStateFlow(EmojiUiState())
+    val uiState: StateFlow<EmojiUiState> = _uiState.asStateFlow()
+
+    private var searchJob: Job? = null
+
+    init {
+        submitSearch("")
+    }
+
+    fun updateQuery(query: String) {
+        _uiState.value = _uiState.value.copy(query = query)
+        submitSearch(query)
+    }
+
+    private fun submitSearch(query: String) {
+        searchJob?.cancel()
+        searchJob = viewModelScope.launch {
+            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
+            // Debounce to avoid rapid queries when typing
+            delay(150)
+            runCatching {
+                repository.searchEmojis(query)
+            }.onSuccess { response ->
+                _uiState.value = _uiState.value.copy(
+                    results = response.results,
+                    suggestions = response.suggestions,
+                    isLoading = false,
+                    errorMessage = null
+                )
+            }.onFailure { throwable ->
+                _uiState.value = _uiState.value.copy(
+                    isLoading = false,
+                    errorMessage = throwable.message ?: "Unable to load emoji data"
+                )
+            }
+        }
+    }
+}
+
+data class EmojiUiState(
+    val query: String = "",
+    val results: List<EmojiDefinition> = emptyList(),
+    val suggestions: List<EmojiSuggestion> = emptyList(),
+    val isLoading: Boolean = false,
+    val errorMessage: String? = null
+)
 
EOF
)

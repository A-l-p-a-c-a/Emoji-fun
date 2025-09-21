package com.example.emojifun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emojifun.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                EmojiFunApp()
            }
        }
    }
}

@Composable
fun EmojiFunApp(viewModel: EmojiViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    EmojiSearchScreen(
        uiState = uiState,
        onQueryChanged = viewModel::updateQuery,
        onSuggestionClicked = { sequence ->
            viewModel.updateQuery(sequence.joinToString(separator = ""))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiSearchScreen(
    uiState: EmojiUiState,
    onQueryChanged: (String) -> Unit,
    onSuggestionClicked: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = uiState.query,
                onValueChange = onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(id = R.string.search_hint)) },
                singleLine = true,
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                colors = TextFieldDefaults.colors()
            )

            if (uiState.isLoading) {
                LinearLoadingIndicator()
            }

            uiState.errorMessage?.let { message ->
                ErrorMessage(message)
            }

            if (uiState.suggestions.isNotEmpty()) {
                SuggestionsSection(uiState.suggestions, onSuggestionClicked)
            }

            EmojiResultsList(uiState)
        }
    }
}

@Composable
private fun LinearLoadingIndicator() {
    RowWithCenteredContent {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(0.6f))
    }
}

@Composable
private fun ErrorMessage(message: String) {
    RowWithCenteredContent {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun SuggestionsSection(
    suggestions: List<EmojiSuggestion>,
    onSuggestionClicked: (List<String>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = R.string.suggestions_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(suggestions) { suggestion ->
                AssistChip(
                    onClick = { onSuggestionClicked(suggestion.sequence) },
                    label = {
                        Text(text = "${suggestion.sequence.joinToString(separator = "")} · ${suggestion.label}")
                    }
                )
            }
        }
    }
}

@Composable
private fun EmojiResultsList(uiState: EmojiUiState) {
    if (uiState.results.isEmpty() && !uiState.isLoading && uiState.errorMessage == null) {
        RowWithCenteredContent {
            Text(text = stringResource(id = R.string.no_results))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.results) { emoji ->
                EmojiResultCard(emoji)
            }
        }
    }
}

@Composable
private fun EmojiResultCard(emojiDefinition: EmojiDefinition) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "${emojiDefinition.char} ${emojiDefinition.name}", style = MaterialTheme.typography.titleMedium)
            Text(
                text = emojiDefinition.keywords.joinToString(separator = ", "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (emojiDefinition.suggestions.isNotEmpty()) {
                Text(
                    text = "Combos: " + emojiDefinition.suggestions.joinToString { it.sequence.joinToString(separator = "") },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun RowWithCenteredContent(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Preview
@Composable
private fun EmojiSearchScreenPreview() {
    val sampleEmoji = EmojiDefinition(
        char = "😀",
        name = "Grinning Face",
        keywords = listOf("smile", "happy"),
        suggestions = listOf(
            EmojiSuggestion("Party", listOf("😀", "🥳"), listOf("party"))
        )
    )
    MaterialTheme {
        EmojiSearchScreen(
            uiState = EmojiUiState(
                query = "smile",
                results = listOf(sampleEmoji),
                suggestions = sampleEmoji.suggestions
            ),
            onQueryChanged = {},
            onSuggestionClicked = {}
        )
    }
}
 

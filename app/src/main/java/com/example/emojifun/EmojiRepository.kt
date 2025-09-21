
package com.example.emojifun

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

class EmojiRepository(private val dataSource: EmojiDefinitionProvider) {

    suspend fun searchEmojis(query: String): EmojiSearchResponse = withContext(Dispatchers.Default) {
        val definitions = dataSource.getEmojiDefinitions()
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) {
            return@withContext EmojiSearchResponse(
                results = definitions.sortedBy { it.name.lowercase() },
                suggestions = gatherTopSuggestions(definitions, emptyList())
            )
        }

        val tokens = normalized.split(" ", "\t", "\n").filter { it.isNotBlank() }
        val scored = definitions.mapNotNull { definition ->
            val score = calculateScore(definition, normalized, tokens)
            if (score > 0) EmojiSearchResult(definition, score) else null
        }.sortedWith(compareByDescending<EmojiSearchResult> { it.score }.thenBy { it.emoji.name.lowercase() })

        val results = scored.map { it.emoji }
        return@withContext EmojiSearchResponse(
            results = results,
            suggestions = gatherTopSuggestions(results, tokens)
        )
    }

    private fun calculateScore(definition: EmojiDefinition, normalizedQuery: String, tokens: List<String>): Int {
        var score = 0
        if (definition.char == normalizedQuery) {
            score += 100
        }
        if (definition.name.lowercase().contains(normalizedQuery)) {
            score += 40
        }
        tokens.forEach { token ->
            if (definition.char.contains(token, ignoreCase = true)) {
                score += 30
            }
            if (definition.name.contains(token, ignoreCase = true)) {
                score += 20
            }
            definition.keywords.forEach { keyword ->
                if (keyword.contains(token, ignoreCase = true)) {
                    score += 10
                }
            }
        }
        return max(score, 0)
    }

    private fun gatherTopSuggestions(
        results: List<EmojiDefinition>,
        queryTokens: List<String>
    ): List<EmojiSuggestion> {
        val scoredSuggestions = mutableMapOf<String, Pair<EmojiSuggestion, Int>>()
        results.forEach { definition ->
            definition.suggestions.forEach { suggestion ->
                val key = suggestion.sequence.joinToString(separator = "") + suggestion.label
                val suggestionScore = calculateSuggestionScore(suggestion, queryTokens)
                if (suggestionScore > 0 || queryTokens.isEmpty()) {
                    val existingScore = scoredSuggestions[key]?.second ?: Int.MIN_VALUE
                    if (suggestionScore > existingScore) {
                        scoredSuggestions[key] = suggestion to suggestionScore
                    }
                }
            }
        }
        return scoredSuggestions.values
            .sortedWith(compareByDescending<Pair<EmojiSuggestion, Int>> { it.second }.thenBy { it.first.label })
            .map { it.first }
            .take(8)
    }

    private fun calculateSuggestionScore(suggestion: EmojiSuggestion, queryTokens: List<String>): Int {
        if (queryTokens.isEmpty()) return 1
        var score = 0
        val labelLower = suggestion.label.lowercase()
        val keywordsLower = suggestion.keywords.map { it.lowercase() }
        val sequenceText = suggestion.sequence.joinToString(separator = "")
        queryTokens.forEach { token ->
            if (labelLower.contains(token)) {
                score += 20
            }
            if (sequenceText.contains(token)) {
                score += 15
            }
            keywordsLower.forEach { keyword ->
                if (keyword.contains(token)) {
                    score += 10
                }
            }
        }
        return score
    }
}
 

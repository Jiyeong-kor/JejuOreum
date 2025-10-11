package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject

class SearchOreumsUseCase @Inject constructor() {

    operator fun invoke(
        oreums: List<ResultSummary>,
        query: String,
    ): Result {
        val sanitized = query.trim()
        if (sanitized.isEmpty()) {
            return Result.EmptyQuery
        }

        val lowerQuery = sanitized.lowercase()
        val matches = oreums.filter { summary ->
            summary.oreumKname.lowercase().contains(lowerQuery) ||
                    summary.oreumAddr.lowercase().contains(lowerQuery)
        }

        return if (matches.isEmpty()) {
            Result.NoMatches(sanitized)
        } else {
            Result.Matches(sanitized, matches)
        }
    }

    sealed interface Result {
        data object EmptyQuery : Result
        data class Matches(val sanitizedQuery: String, val results: List<ResultSummary>) : Result
        data class NoMatches(val sanitizedQuery: String) : Result
    }
}

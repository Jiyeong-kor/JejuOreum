package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject

class SearchOreumsUseCase @Inject constructor() {
    operator fun invoke(oreums: List<ResultSummary>, query: String): List<ResultSummary> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()

        val lowerQuery = trimmed.lowercase()
        return oreums.filter { summary ->
            summary.oreumKname.lowercase().contains(lowerQuery) ||
                    summary.oreumAddr.lowercase().contains(lowerQuery)
        }
    }
}

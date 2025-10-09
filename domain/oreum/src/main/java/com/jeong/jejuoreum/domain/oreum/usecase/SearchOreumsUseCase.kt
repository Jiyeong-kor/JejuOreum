package com.jeong.jejuoreum.domain.usecase.oreum

import com.jeong.jejuoreum.domain.entity.ResultSummary
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

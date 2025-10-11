package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.usecase.SearchOreumsUseCase
import com.jeong.jejuoreum.feature.map.presentation.model.OreumSummaryUiMapper
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class MapSearchHandler @Inject constructor(
    private val searchOreumsUseCase: SearchOreumsUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val summaryUiMapper: OreumSummaryUiMapper,
) {

    suspend fun search(query: String, summaries: List<ResultSummary>): MapSearchResult =
        withContext(dispatcherProvider.computation) {
            val sanitized = query.trim()
            if (sanitized.isEmpty()) {
                MapSearchResult(emptyList(), MapPanelState.Hidden)
            } else {
                val matches = searchOreumsUseCase(summaries, sanitized)
                    .map(summaryUiMapper::map)
                val panelState = if (matches.isEmpty()) {
                    MapPanelState.NoResults
                } else {
                    MapPanelState.Results
                }
                MapSearchResult(matches, panelState)
            }
        }
}

internal data class MapSearchResult(
    val results: List<OreumSummaryUiModel>,
    val panelState: MapPanelState,
)

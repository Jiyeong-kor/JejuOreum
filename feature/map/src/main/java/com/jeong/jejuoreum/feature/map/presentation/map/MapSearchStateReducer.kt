package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.core.presentation.state.StateReducer
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject

internal class MapSearchStateReducer @Inject constructor(
    private val summaryUiMapper: OreumSummaryUiMapper,
) : StateReducer<SearchState, MapSearchChange> {

    override fun reduce(current: SearchState, change: MapSearchChange): SearchState =
        when (change) {
            MapSearchChange.PanelClosed -> SearchState()
            MapSearchChange.EmptyQuery -> current.copy(
                query = "",
                searchResults = emptyList(),
                panelState = MapPanelState.Hidden,
            )

            is MapSearchChange.Matches -> current.copy(
                query = change.query,
                searchResults = change.results.toUiModels(),
                panelState = MapPanelState.Results,
            )

            is MapSearchChange.NoMatches -> current.copy(
                query = change.query,
                searchResults = emptyList(),
                panelState = MapPanelState.NoResults,
            )
        }

    private fun List<ResultSummary>.toUiModels() = map(summaryUiMapper::map)
}

internal sealed interface MapSearchChange {
    data object PanelClosed : MapSearchChange
    data object EmptyQuery : MapSearchChange
    data class Matches(val query: String, val results: List<ResultSummary>) : MapSearchChange
    data class NoMatches(val query: String) : MapSearchChange
}

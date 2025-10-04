package com.jeong.feature.oreum.presentation.map

import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel

sealed class MapUiState {
    data object Idle : MapUiState()
    data class SearchResults(val list: List<OreumSummaryUiModel>) : MapUiState()
    data object NoResults : MapUiState()
    data object Hidden : MapUiState()
}

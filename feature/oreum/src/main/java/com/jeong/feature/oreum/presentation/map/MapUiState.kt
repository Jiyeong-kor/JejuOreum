package com.jeong.feature.oreum.presentation.map

import com.jeong.domain.entity.ResultSummary

sealed class MapUiState {
    data object Idle : MapUiState()
    data class SearchResults(val list: List<ResultSummary>) : MapUiState()
    data object NoResults : MapUiState()
    data object Hidden : MapUiState()
}

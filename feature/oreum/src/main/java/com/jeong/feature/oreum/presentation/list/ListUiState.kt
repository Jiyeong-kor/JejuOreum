package com.jeong.feature.oreum.presentation.list

import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel

data class ListUiState(
    val oreums: List<OreumSummaryUiModel> = emptyList(),
    val isLoading: Boolean = false
)

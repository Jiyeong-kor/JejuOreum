package com.jeong.feature.oreum.presentation.profile.favorite

import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel

data class MyFavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<OreumSummaryUiModel> = emptyList(),
    val errorMessage: String? = null
)

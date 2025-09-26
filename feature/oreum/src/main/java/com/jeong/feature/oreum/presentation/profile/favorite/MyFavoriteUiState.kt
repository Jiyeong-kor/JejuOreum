package com.jeong.feature.oreum.presentation.profile.favorite

import com.jeong.domain.entity.ResultSummary

data class MyFavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<ResultSummary> = emptyList(),
    val errorMessage: String? = null
)

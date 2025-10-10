package com.jeong.jejuoreum.feature.profile.presentation.favorite

import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel

data class MyFavoriteUiState(
    val isLoading: Boolean = true,
    val favorites: List<OreumSummaryUiModel> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface MyFavoriteUiEvent {
    data object Initialize : MyFavoriteUiEvent
    data class FavoriteToggled(val oreumIdx: String, val currentlyLiked: Boolean) : MyFavoriteUiEvent
    data object ErrorConsumed : MyFavoriteUiEvent
}

sealed interface MyFavoriteUiEffect {
    data class ShowError(val message: String) : MyFavoriteUiEffect
}

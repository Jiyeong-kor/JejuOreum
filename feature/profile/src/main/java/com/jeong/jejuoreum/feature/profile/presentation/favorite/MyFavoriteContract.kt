package com.jeong.jejuoreum.feature.profile.presentation.favorite

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import com.jeong.jejuoreum.core.architecture.UiText
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel

data class MyFavoriteUiState(
    val isLoading: Boolean = true,
    val favorites: List<OreumSummaryUiModel> = emptyList(),
    val errorMessage: UiText? = null,
) : UiState

sealed interface MyFavoriteUiEvent : UiEvent {
    data object Initialize : MyFavoriteUiEvent
    data class FavoriteToggled(val oreumIdx: String, val currentlyLiked: Boolean) : MyFavoriteUiEvent
    data object ErrorConsumed : MyFavoriteUiEvent
}

sealed interface MyFavoriteUiEffect : UiEffect {
    data class ShowError(val message: UiText) : MyFavoriteUiEffect
}

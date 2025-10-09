package com.jeong.jejuoreum.feature.map.presentation.viewmodel

import com.jeong.jejuoreum.core.ui.state.UiEffect
import com.jeong.jejuoreum.core.ui.state.UiEvent
import com.jeong.jejuoreum.core.ui.state.UiState
import com.jeong.jejuoreum.feature.map.presentation.model.OreumUiModel

sealed interface OreumEvent : UiEvent {
    data object ScreenInitialized : OreumEvent
    data class OreumSelected(val id: String) : OreumEvent
    data object RefreshRequested : OreumEvent
}

sealed interface OreumEffect : UiEffect {
    data class NavigateToDetail(val oreumId: String) : OreumEffect
    data class ShowError(val message: String) : OreumEffect
}

data class OreumUiState(
    val isLoading: Boolean = false,
    val oreums: List<OreumUiModel> = emptyList(),
    val errorMessage: String? = null
) : UiState

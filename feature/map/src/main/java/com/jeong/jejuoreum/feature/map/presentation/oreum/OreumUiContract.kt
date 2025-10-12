package com.jeong.jejuoreum.feature.map.presentation.oreum

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import com.jeong.jejuoreum.core.common.UiText
import com.jeong.jejuoreum.feature.map.presentation.model.OreumUiModel

sealed interface OreumEvent : UiEvent {
    data object ScreenInitialized : OreumEvent
    data class OreumSelected(val id: String) : OreumEvent
    data object RefreshRequested : OreumEvent
}

sealed interface OreumEffect : UiEffect {
    data class NavigateToDetail(val oreumId: String) : OreumEffect
    data class ShowError(val message: UiText) : OreumEffect
}

data class OreumUiState(
    val isLoading: Boolean = false,
    val oreums: List<OreumUiModel> = emptyList(),
    val errorMessage: UiText? = null,
) : UiState

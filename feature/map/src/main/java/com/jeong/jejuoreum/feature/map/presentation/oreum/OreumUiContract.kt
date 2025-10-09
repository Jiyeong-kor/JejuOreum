package com.jeong.jejuoreum.feature.map.presentation.oreum

import com.jeong.jejuoreum.feature.map.presentation.model.OreumUiModel

sealed interface OreumEvent {
    data object ScreenInitialized : OreumEvent
    data class OreumSelected(val id: String) : OreumEvent
    data object RefreshRequested : OreumEvent
}

sealed interface OreumEffect {
    data class NavigateToDetail(val oreumId: String) : OreumEffect
    data class ShowError(val message: String) : OreumEffect
}

data class OreumUiState(
    val isLoading: Boolean = false,
    val oreums: List<OreumUiModel> = emptyList(),
    val errorMessage: String? = null
)

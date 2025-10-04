package com.jeong.feature.oreum.presentation.list

import com.jeong.core.ui.state.UiEffect
import com.jeong.core.ui.state.UiEvent
import com.jeong.core.ui.state.UiState
import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel

data class ListUiState(
    val oreums: List<OreumSummaryUiModel> = emptyList(),
    val isLoading: Boolean = false,
) : UiState {
    val isEmpty: Boolean get() = oreums.isEmpty() && !isLoading
}

sealed interface ListUiEvent : UiEvent {
    data object ScreenInitialized : ListUiEvent
    data class FavoriteToggled(val summary: OreumSummaryUiModel) : ListUiEvent
    data class StampRequested(val summary: OreumSummaryUiModel) : ListUiEvent
    data object RefreshRequested : ListUiEvent
}

sealed interface ListUiEffect : UiEffect {
    data object StampSuccess : ListUiEffect
    data class StampFailure(val reason: String?) : ListUiEffect
    data class LoadFailed(val reason: String?) : ListUiEffect
    data class FavoriteUpdateFailed(val reason: String?) : ListUiEffect
}

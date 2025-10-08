package com.jeong.feature.splash.presentation

import com.jeong.core.ui.state.UiEffect
import com.jeong.core.ui.state.UiEvent
import com.jeong.core.ui.state.UiState
import com.jeong.feature.splash.domain.model.SplashDestination

data class SplashUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) : UiState

sealed interface SplashUiEvent : UiEvent {
    data object Initialize : SplashUiEvent
    data object ErrorConsumed : SplashUiEvent
}

sealed interface SplashSideEffect : UiEffect {
    data class NavigateTo(val destination: SplashDestination) : SplashSideEffect
}

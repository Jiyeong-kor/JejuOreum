package com.jeong.jejuoreum.feature.splash.presentation

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import com.jeong.jejuoreum.core.architecture.UiText
import com.jeong.jejuoreum.feature.splash.domain.model.SplashDestination

data class SplashUiState(
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
) : UiState

sealed interface SplashUiEvent : UiEvent {
    data object Initialize : SplashUiEvent
    data object ErrorConsumed : SplashUiEvent
}

sealed interface SplashUiEffect : UiEffect {
    data class NavigateTo(val destination: SplashDestination) : SplashUiEffect
}

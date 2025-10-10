package com.jeong.jejuoreum.feature.splash.presentation

import com.jeong.jejuoreum.feature.splash.domain.model.SplashDestination

data class SplashUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) 

sealed interface SplashUiEvent {
    data object Initialize : SplashUiEvent
    data object ErrorConsumed : SplashUiEvent
}

sealed interface SplashUiEffect {
    data class NavigateTo(val destination: SplashDestination) : SplashUiEffect
}

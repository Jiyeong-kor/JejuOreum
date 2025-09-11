package com.jeong.feature.splash.presentation

sealed class SplashUiState {
    data object GoToMap : SplashUiState()
    data object GoToJoin : SplashUiState()
}

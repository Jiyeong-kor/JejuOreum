package com.jeong.jjoreum.presentation.ui.splash

sealed class SplashUiState {
    data object GoToMap : SplashUiState()
    data object GoToJoin : SplashUiState()
}
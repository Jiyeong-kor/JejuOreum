package com.jeong.feature.splash.presentation

import com.jeong.feature.splash.domain.model.SplashDestination

data class SplashUiState(
    val isLoading: Boolean = true,
    val destination: SplashDestination? = null,
    val errorMessage: String? = null,
)

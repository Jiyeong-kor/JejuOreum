package com.jeong.jejuoreum.feature.splash.domain.model

sealed interface SplashDestination {
    data object Map : SplashDestination
    data object Join : SplashDestination
}

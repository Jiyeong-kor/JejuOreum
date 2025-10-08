package com.jeong.feature.splash.domain

interface SplashInitializer {
    suspend fun initialize(): Result<Unit>
}

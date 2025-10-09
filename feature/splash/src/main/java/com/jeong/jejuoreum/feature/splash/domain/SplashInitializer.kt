package com.jeong.jejuoreum.feature.splash.domain

interface SplashInitializer {
    suspend fun initialize(): Result<Unit>
}

package com.jeong.feature.splash.domain.usecase

import com.jeong.domain.usecase.oreum.PreloadOreumSummariesUseCase
import com.jeong.feature.splash.domain.SplashInitializer
import com.jeong.feature.splash.domain.UserStatusChecker
import com.jeong.feature.splash.domain.model.SplashDestination
import javax.inject.Inject

class PrepareSplashUseCase @Inject constructor(
    private val splashInitializer: SplashInitializer,
    private val userStatusChecker: UserStatusChecker,
    private val preloadOreumSummariesUseCase: PreloadOreumSummariesUseCase,
) {
    suspend operator fun invoke(): Result<SplashDestination> {
        return runCatching {
            splashInitializer.initialize().getOrThrow()
            preloadOreumSummariesUseCase().getOrThrow()
            if (userStatusChecker.isUserRegistered()) {
                SplashDestination.Map
            } else {
                SplashDestination.Join
            }
        }
    }
}

package com.jeong.jejuoreum.feature.splash.domain

import com.jeong.jejuoreum.domain.oreum.usecase.PreloadOreumSummariesUseCase
import com.jeong.jejuoreum.feature.splash.domain.model.SplashDestination
import javax.inject.Inject

class PrepareSplashInteractor @Inject constructor(
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

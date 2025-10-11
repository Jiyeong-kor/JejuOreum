package com.jeong.jejuoreum.feature.splash.domain

import com.jeong.jejuoreum.domain.oreum.usecase.PreloadOreumSummariesUseCase
import com.jeong.jejuoreum.domain.user.usecase.IsUserRegisteredUseCase
import com.jeong.jejuoreum.feature.splash.domain.model.SplashDestination
import javax.inject.Inject

class PrepareSplashInteractor @Inject constructor(
    private val splashInitializer: SplashInitializer,
    private val preloadOreumSummariesUseCase: PreloadOreumSummariesUseCase,
    private val isUserRegisteredUseCase: IsUserRegisteredUseCase,
) {
    suspend operator fun invoke(): Result<SplashDestination> = runCatching {
        splashInitializer.initialize().getOrThrow()
        preloadOreumSummariesUseCase().getOrThrow()
        val isRegistered = isUserRegisteredUseCase().getOrThrow()
        if (isRegistered) {
            SplashDestination.Map
        } else {
            SplashDestination.Join
        }
    }
}

package com.jeong.feature.splash.di

import com.jeong.domain.repository.OreumRepository
import com.jeong.domain.usecase.oreum.PreloadOreumSummariesUseCase
import com.jeong.feature.splash.domain.SplashInitializer
import com.jeong.feature.splash.domain.UserStatusChecker
import com.jeong.feature.splash.domain.usecase.PrepareSplashUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object SplashUseCaseModule {
    @Provides
    fun providePreloadOreumSummariesUseCase(
        oreumRepository: OreumRepository,
    ): PreloadOreumSummariesUseCase = PreloadOreumSummariesUseCase(oreumRepository)

    @Provides
    fun providePrepareSplashUseCase(
        splashInitializer: SplashInitializer,
        userStatusChecker: UserStatusChecker,
        preloadOreumSummariesUseCase: PreloadOreumSummariesUseCase,
    ): PrepareSplashUseCase = PrepareSplashUseCase(
        splashInitializer,
        userStatusChecker,
        preloadOreumSummariesUseCase,
    )
}

package com.jeong.jejuoreum.feature.splash.di

import com.jeong.jejuoreum.domain.repository.OreumRepository
import com.jeong.jejuoreum.domain.usecase.oreum.PreloadOreumSummariesUseCase
import com.jeong.jejuoreum.feature.splash.domain.SplashInitializer
import com.jeong.jejuoreum.feature.splash.domain.UserStatusChecker
import com.jeong.jejuoreum.feature.splash.domain.usecase.PrepareSplashUseCase
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

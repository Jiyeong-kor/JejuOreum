package com.jeong.feature.oreum.di

import com.jeong.domain.repository.OreumRepository
import com.jeong.domain.repository.StampRepository
import com.jeong.domain.repository.UserInteractionRepository
import com.jeong.domain.usecase.ToggleFavoriteUseCase
import com.jeong.feature.oreum.domain.usecase.FilterOreumsWithinBoundsUseCase
import com.jeong.feature.oreum.domain.usecase.FindOreumByLocationUseCase
import com.jeong.feature.oreum.domain.usecase.GetUserStampStatusesUseCase
import com.jeong.feature.oreum.domain.usecase.LoadOreumSummariesUseCase
import com.jeong.feature.oreum.domain.usecase.ObserveOreumSummariesUseCase
import com.jeong.feature.oreum.domain.usecase.SearchOreumsUseCase
import com.jeong.feature.oreum.domain.usecase.TryStampUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object OreumUseCaseModule {
    @Provides
    fun provideToggleFavoriteUseCase(
        userInteractionRepository: UserInteractionRepository,
        oreumRepository: OreumRepository
    ): ToggleFavoriteUseCase = ToggleFavoriteUseCase(
        userInteractionRepository,
        oreumRepository
    )

    @Provides
    fun provideObserveOreumSummariesUseCase(
        oreumRepository: OreumRepository,
    ): ObserveOreumSummariesUseCase = ObserveOreumSummariesUseCase(oreumRepository)

    @Provides
    fun provideLoadOreumSummariesUseCase(
        oreumRepository: OreumRepository,
    ): LoadOreumSummariesUseCase = LoadOreumSummariesUseCase(oreumRepository)

    @Provides
    fun provideFilterOreumsWithinBoundsUseCase(): FilterOreumsWithinBoundsUseCase =
        FilterOreumsWithinBoundsUseCase()

    @Provides
    fun provideSearchOreumsUseCase(): SearchOreumsUseCase = SearchOreumsUseCase()

    @Provides
    fun provideFindOreumByLocationUseCase(): FindOreumByLocationUseCase =
        FindOreumByLocationUseCase()

    @Provides
    fun provideTryStampUseCase(
        stampRepository: StampRepository,
    ): TryStampUseCase = TryStampUseCase(stampRepository)

    @Provides
    fun provideGetUserStampStatusesUseCase(
        userInteractionRepository: UserInteractionRepository,
    ): GetUserStampStatusesUseCase = GetUserStampStatusesUseCase(userInteractionRepository)
}

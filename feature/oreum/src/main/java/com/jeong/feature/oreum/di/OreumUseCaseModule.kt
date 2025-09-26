package com.jeong.feature.oreum.di

import com.jeong.domain.repository.OreumRepository
import com.jeong.domain.repository.ReviewRepository
import com.jeong.domain.repository.StampRepository
import com.jeong.domain.repository.UserInteractionRepository
import com.jeong.domain.usecase.ToggleFavoriteUseCase
import com.jeong.feature.oreum.domain.usecase.FetchOreumDetailUseCase
import com.jeong.feature.oreum.domain.usecase.FilterOreumsWithinBoundsUseCase
import com.jeong.feature.oreum.domain.usecase.FindOreumByLocationUseCase
import com.jeong.feature.oreum.domain.usecase.GetCurrentUserNicknameUseCase
import com.jeong.feature.oreum.domain.usecase.GetOreumFavoriteStatusUseCase
import com.jeong.feature.oreum.domain.usecase.GetOreumReviewsUseCase
import com.jeong.feature.oreum.domain.usecase.GetOreumStampStatusUseCase
import com.jeong.feature.oreum.domain.usecase.GetUserStampStatusesUseCase
import com.jeong.feature.oreum.domain.usecase.LoadOreumSummariesUseCase
import com.jeong.feature.oreum.domain.usecase.LoadStampedOreumsUseCase
import com.jeong.feature.oreum.domain.usecase.ObserveFavoriteOreumsUseCase
import com.jeong.feature.oreum.domain.usecase.ObserveOreumSummariesUseCase
import com.jeong.feature.oreum.domain.usecase.RefreshOreumSummariesUseCase
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
    fun provideObserveFavoriteOreumsUseCase(
        oreumRepository: OreumRepository,
    ): ObserveFavoriteOreumsUseCase = ObserveFavoriteOreumsUseCase(oreumRepository)

    @Provides
    fun provideLoadOreumSummariesUseCase(
        oreumRepository: OreumRepository,
    ): LoadOreumSummariesUseCase = LoadOreumSummariesUseCase(oreumRepository)

    @Provides
    fun provideRefreshOreumSummariesUseCase(
        oreumRepository: OreumRepository,
    ): RefreshOreumSummariesUseCase = RefreshOreumSummariesUseCase(oreumRepository)

    @Provides
    fun provideLoadStampedOreumsUseCase(
        oreumRepository: OreumRepository,
    ): LoadStampedOreumsUseCase = LoadStampedOreumsUseCase(oreumRepository)

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

    @Provides
    fun provideFetchOreumDetailUseCase(
        oreumRepository: OreumRepository,
    ): FetchOreumDetailUseCase = FetchOreumDetailUseCase(oreumRepository)

    @Provides
    fun provideGetOreumFavoriteStatusUseCase(
        userInteractionRepository: UserInteractionRepository,
    ): GetOreumFavoriteStatusUseCase = GetOreumFavoriteStatusUseCase(userInteractionRepository)

    @Provides
    fun provideGetOreumStampStatusUseCase(
        userInteractionRepository: UserInteractionRepository,
    ): GetOreumStampStatusUseCase = GetOreumStampStatusUseCase(userInteractionRepository)

    @Provides
    fun provideGetOreumReviewsUseCase(
        reviewRepository: ReviewRepository,
    ): GetOreumReviewsUseCase = GetOreumReviewsUseCase(reviewRepository)

    @Provides
    fun provideGetCurrentUserNicknameUseCase(
        userInteractionRepository: UserInteractionRepository,
    ): GetCurrentUserNicknameUseCase = GetCurrentUserNicknameUseCase(userInteractionRepository)
}

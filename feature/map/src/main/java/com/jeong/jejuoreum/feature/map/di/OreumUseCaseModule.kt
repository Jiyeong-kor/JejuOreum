package com.jeong.feature.oreum.di

import com.jeong.domain.repository.OreumRepository
import com.jeong.domain.repository.PermissionRepository
import com.jeong.domain.repository.ReviewRepository
import com.jeong.domain.repository.StampRepository
import com.jeong.domain.repository.UserInteractionRepository
import com.jeong.domain.usecase.IsLocationPermissionGrantedUseCase
import com.jeong.domain.usecase.UpdateLocationPermissionUseCase
import com.jeong.domain.usecase.oreum.FetchOreumDetailUseCase
import com.jeong.domain.usecase.oreum.FilterOreumsWithinBoundsUseCase
import com.jeong.domain.usecase.oreum.FindOreumByLocationUseCase
import com.jeong.domain.usecase.oreum.GetCurrentUserNicknameUseCase
import com.jeong.domain.usecase.oreum.GetOreumFavoriteStatusUseCase
import com.jeong.domain.usecase.oreum.GetOreumReviewsUseCase
import com.jeong.domain.usecase.oreum.GetOreumStampStatusUseCase
import com.jeong.domain.usecase.oreum.GetUserStampStatusesUseCase
import com.jeong.domain.usecase.oreum.LoadOreumSummariesUseCase
import com.jeong.domain.usecase.oreum.LoadStampedOreumsUseCase
import com.jeong.domain.usecase.oreum.ObserveFavoriteOreumsUseCase
import com.jeong.domain.usecase.oreum.ObserveOreumSummariesUseCase
import com.jeong.domain.usecase.oreum.RefreshOreumSummariesUseCase
import com.jeong.domain.usecase.oreum.SearchOreumsUseCase
import com.jeong.domain.usecase.oreum.ToggleFavoriteUseCase
import com.jeong.domain.usecase.oreum.TryStampUseCase
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

    @Provides
    fun provideIsLocationPermissionGrantedUseCase(
        permissionRepository: PermissionRepository,
    ): IsLocationPermissionGrantedUseCase = IsLocationPermissionGrantedUseCase(permissionRepository)

    @Provides
    fun provideUpdateLocationPermissionUseCase(
        permissionRepository: PermissionRepository,
    ): UpdateLocationPermissionUseCase = UpdateLocationPermissionUseCase(permissionRepository)
}

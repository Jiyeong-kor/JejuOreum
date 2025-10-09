package com.jeong.jejuoreum.feature.map.di

import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import com.jeong.jejuoreum.domain.user.repository.PermissionRepository
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import com.jeong.jejuoreum.domain.oreum.repository.StampRepository
import com.jeong.jejuoreum.domain.user.repository.UserInteractionRepository
import com.jeong.jejuoreum.domain.user.usecase.IsLocationPermissionGrantedUseCase
import com.jeong.jejuoreum.domain.user.usecase.UpdateLocationPermissionUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.FetchOreumDetailUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.FilterOreumsWithinBoundsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.FindOreumByLocationUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.GetCurrentUserNicknameUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumFavoriteStatusUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumReviewsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumStampStatusUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.GetUserStampStatusesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.LoadOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.LoadStampedOreumsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveFavoriteOreumsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.RefreshOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.SearchOreumsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ToggleFavoriteUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.TryStampUseCase
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

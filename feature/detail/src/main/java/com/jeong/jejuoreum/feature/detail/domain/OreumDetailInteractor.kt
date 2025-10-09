package com.jeong.jejuoreum.feature.detail.domain

import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.user.usecase.IsLocationPermissionGrantedUseCase
import com.jeong.jejuoreum.domain.user.usecase.UpdateLocationPermissionUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.FetchOreumDetailUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumFavoriteStatusUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumReviewsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumStampStatusUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ToggleFavoriteUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.TryStampUseCase
import com.jeong.jejuoreum.feature.detail.domain.model.OreumStampRequest
import javax.inject.Inject

interface OreumDetailInteractor {
    suspend fun fetchOreumDetail(oreumIdx: String): Result<ResultSummary>
    suspend fun fetchFavoriteStatus(oreumIdx: String): Result<Boolean>
    suspend fun fetchStampStatus(oreumIdx: String): Result<Boolean>
    suspend fun fetchReviews(oreumIdx: String): Result<List<ReviewItem>>
    suspend fun toggleFavorite(oreumIdx: String, newIsFavorite: Boolean): Result<Int>
    suspend fun tryStamp(request: OreumStampRequest): Result<Unit>
    suspend fun loadLocationPermissionState(): Result<Boolean>
    suspend fun updateLocationPermission(granted: Boolean): Result<Unit>
}

class DefaultOreumDetailInteractor @Inject constructor(
    private val fetchOreumDetailUseCase: FetchOreumDetailUseCase,
    private val getOreumFavoriteStatusUseCase: GetOreumFavoriteStatusUseCase,
    private val getOreumStampStatusUseCase: GetOreumStampStatusUseCase,
    private val getOreumReviewsUseCase: GetOreumReviewsUseCase,
    private val tryStampUseCase: TryStampUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isLocationPermissionGrantedUseCase: IsLocationPermissionGrantedUseCase,
    private val updateLocationPermissionUseCase: UpdateLocationPermissionUseCase,
) : OreumDetailInteractor {

    override suspend fun fetchOreumDetail(oreumIdx: String): Result<ResultSummary> =
        fetchOreumDetailUseCase(oreumIdx)

    override suspend fun fetchFavoriteStatus(oreumIdx: String): Result<Boolean> =
        getOreumFavoriteStatusUseCase(oreumIdx)

    override suspend fun fetchStampStatus(oreumIdx: String): Result<Boolean> =
        getOreumStampStatusUseCase(oreumIdx)

    override suspend fun fetchReviews(oreumIdx: String): Result<List<ReviewItem>> =
        getOreumReviewsUseCase(oreumIdx)

    override suspend fun toggleFavorite(oreumIdx: String, newIsFavorite: Boolean): Result<Int> =
        runCatching { toggleFavoriteUseCase(oreumIdx, newIsFavorite) }

    override suspend fun tryStamp(request: OreumStampRequest): Result<Unit> =
        tryStampUseCase(
            oreumIdx = request.oreumIdx,
            oreumName = request.oreumName,
            oreumLat = request.latitude,
            oreumLng = request.longitude
        )

    override suspend fun loadLocationPermissionState(): Result<Boolean> =
        isLocationPermissionGrantedUseCase()

    override suspend fun updateLocationPermission(granted: Boolean): Result<Unit> =
        updateLocationPermissionUseCase(granted)
}

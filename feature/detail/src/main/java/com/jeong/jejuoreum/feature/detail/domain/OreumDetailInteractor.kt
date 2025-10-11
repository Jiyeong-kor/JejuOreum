package com.jeong.jejuoreum.feature.detail.domain

import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.usecase.FetchOreumDetailUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.RefreshOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.TryStampUseCase
import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.review.usecase.FetchReviewsUseCase
import com.jeong.jejuoreum.domain.user.usecase.GetOreumFavoriteStatusUseCase
import com.jeong.jejuoreum.domain.user.usecase.GetOreumStampStatusUseCase
import com.jeong.jejuoreum.domain.user.usecase.IsLocationPermissionGrantedUseCase
import com.jeong.jejuoreum.domain.user.usecase.ToggleFavoriteUseCase
import com.jeong.jejuoreum.domain.user.usecase.UpdateLocationPermissionUseCase
import com.jeong.jejuoreum.feature.detail.domain.model.OreumStampRequest
import javax.inject.Inject

class OreumDetailInteractor @Inject constructor(
    private val fetchOreumDetailUseCase: FetchOreumDetailUseCase,
    private val getOreumFavoriteStatusUseCase: GetOreumFavoriteStatusUseCase,
    private val getOreumStampStatusUseCase: GetOreumStampStatusUseCase,
    private val fetchReviewsUseCase: FetchReviewsUseCase,
    private val tryStampUseCase: TryStampUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val refreshOreumSummariesUseCase: RefreshOreumSummariesUseCase,
    private val isLocationPermissionGrantedUseCase: IsLocationPermissionGrantedUseCase,
    private val updateLocationPermissionUseCase: UpdateLocationPermissionUseCase,
) {

    suspend fun fetchOreumDetail(oreumIdx: String): Result<ResultSummary> =
        fetchOreumDetailUseCase(oreumIdx)

    suspend fun fetchFavoriteStatus(oreumIdx: String): Result<Boolean> =
        getOreumFavoriteStatusUseCase(oreumIdx)

    suspend fun fetchStampStatus(oreumIdx: String): Result<Boolean> =
        getOreumStampStatusUseCase(oreumIdx)

    suspend fun fetchReviews(oreumIdx: String): Result<List<ReviewItem>> =
        fetchReviewsUseCase(oreumIdx)

    suspend fun toggleFavorite(oreumIdx: String, newIsFavorite: Boolean): Result<Int> {
        val toggleResult = toggleFavoriteUseCase(oreumIdx, newIsFavorite)
        val newTotal = toggleResult.getOrNull() ?: return toggleResult

        return refreshOreumSummariesUseCase()
            .map { newTotal }
    }

    suspend fun tryStamp(request: OreumStampRequest): Result<Unit> =
        tryStampUseCase(
            oreumIdx = request.oreumIdx,
            oreumName = request.oreumName,
            oreumLat = request.latitude,
            oreumLng = request.longitude
        )

    suspend fun loadLocationPermissionState(): Result<Boolean> =
        isLocationPermissionGrantedUseCase()

    suspend fun updateLocationPermission(granted: Boolean): Result<Unit> =
        updateLocationPermissionUseCase(granted)
}

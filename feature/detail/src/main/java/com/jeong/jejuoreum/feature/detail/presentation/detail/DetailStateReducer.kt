package com.jeong.feature.oreum.presentation.detail

import com.jeong.domain.entity.ReviewItem
import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel
import javax.inject.Inject

class DetailStateReducer @Inject constructor() {

    fun initialize(state: DetailUiState, oreum: OreumSummaryUiModel): DetailUiState =
        state.copy(
            oreumDetail = oreum,
            isFavorite = oreum.userLiked,
            hasStamp = oreum.userStamped,
            errorMessage = null
        )

    fun onLoading(state: DetailUiState): DetailUiState =
        state.copy(isLoading = true, errorMessage = null)

    fun onDetailLoaded(state: DetailUiState, detail: OreumSummaryUiModel): DetailUiState =
        state.copy(
            oreumDetail = detail,
            isFavorite = detail.userLiked,
            hasStamp = detail.userStamped,
            isLoading = false,
            errorMessage = null
        )

    fun onDetailLoadFailed(state: DetailUiState, message: String?): DetailUiState =
        state.copy(isLoading = false, errorMessage = message)

    fun onFavoriteStatusChanged(state: DetailUiState, isFavorite: Boolean): DetailUiState =
        state.copy(
            isFavorite = isFavorite,
            oreumDetail = state.oreumDetail?.copy(userLiked = isFavorite),
        )

    fun onStampStatusChanged(state: DetailUiState, hasStamp: Boolean): DetailUiState =
        state.copy(
            hasStamp = hasStamp,
            oreumDetail = state.oreumDetail?.copy(userStamped = hasStamp),
        )

    fun onReviewsLoaded(state: DetailUiState, reviews: List<ReviewItem>): DetailUiState =
        state.copy(reviewList = reviews)

    fun onFavoriteToggled(state: DetailUiState, isFavorite: Boolean, newTotal: Int): DetailUiState =
        state.copy(
            isFavorite = isFavorite,
            oreumDetail = state.oreumDetail?.copy(
                userLiked = isFavorite,
                totalFavorites = newTotal
            )
        )

    fun onLocationPermissionChanged(state: DetailUiState, granted: Boolean): DetailUiState =
        state.copy(isLocationPermissionGranted = granted)

    fun onError(state: DetailUiState, message: String?): DetailUiState =
        state.copy(errorMessage = message)

    fun clearError(state: DetailUiState): DetailUiState =
        state.copy(errorMessage = null)
}

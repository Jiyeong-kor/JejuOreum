package com.jeong.jejuoreum.feature.detail.presentation.detail

import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import javax.inject.Inject

class DetailStateReducer @Inject constructor() {

    fun initialize(state: DetailUiState, oreum: OreumSummaryUiModel): DetailUiState =
        state.copy(
            oreumDetail = oreum,
            isFavorite = oreum.userLiked,
            hasStamp = oreum.userStamped,
        )

    fun onLoading(state: DetailUiState): DetailUiState =
        state.copy(isLoading = true)

    fun onDetailLoaded(state: DetailUiState, detail: OreumSummaryUiModel): DetailUiState =
        state.copy(
            oreumDetail = detail,
            isFavorite = detail.userLiked,
            hasStamp = detail.userStamped,
            isLoading = false,
        )

    fun onDetailLoadFailed(state: DetailUiState): DetailUiState =
        state.copy(isLoading = false)

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
}

package com.jeong.jejuoreum.feature.detail.presentation.detail

import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.review.entity.ReviewItem

data class DetailUiState(
    val isLoading: Boolean = false,
    val oreumDetail: OreumSummaryUiModel? = null,
    val isFavorite: Boolean = false,
    val hasStamp: Boolean = false,
    val reviewList: List<ReviewItem> = emptyList(),
    val isLocationPermissionGranted: Boolean? = null,
)

sealed interface DetailEvent {
    data class Initialize(val oreum: OreumSummaryUiModel) : DetailEvent
    data object FavoriteClicked : DetailEvent
    data object StampRequested : DetailEvent
    data object ReviewsRequested : DetailEvent
    data class LocationPermissionResult(val granted: Boolean) : DetailEvent
}

sealed interface DetailEffect {
    data class ShowMessage(val message: String) : DetailEffect
    data class FavoriteStatusChanged(val oreumIdx: String, val isFavorite: Boolean) : DetailEffect
    data class StampCompleted(val oreumIdx: String) : DetailEffect
}

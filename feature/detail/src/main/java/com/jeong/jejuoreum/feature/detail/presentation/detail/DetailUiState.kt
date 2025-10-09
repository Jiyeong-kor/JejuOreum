package com.jeong.jejuoreum.feature.detail.presentation.detail

import com.jeong.jejuoreum.feature.detail.presentation.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.review.entity.ReviewItem

data class DetailUiState(
    val isLoading: Boolean = false,
    val oreumDetail: OreumSummaryUiModel? = null,
    val isFavorite: Boolean = false,
    val hasStamp: Boolean = false,
    val reviewList: List<ReviewItem> = emptyList(),
    val errorMessage: String? = null,
    val isLocationPermissionGranted: Boolean? = null
)

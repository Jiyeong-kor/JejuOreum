package com.jeong.feature.oreum.presentation.detail

import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel
import com.jeong.domain.entity.ReviewItem

data class DetailUiState(
    val isLoading: Boolean = false,
    val oreumDetail: OreumSummaryUiModel? = null,
    val isFavorite: Boolean = false,
    val hasStamp: Boolean = false,
    val reviewList: List<ReviewItem> = emptyList(),
    val errorMessage: String? = null,
    val isLocationPermissionGranted: Boolean? = null
)

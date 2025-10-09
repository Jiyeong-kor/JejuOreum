package com.jeong.jejuoreum.feature.map.presentation.model

import com.jeong.jejuoreum.domain.oreum.model.OreumDifficulty

data class OreumUiModel(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val elevation: String,
    val difficulty: OreumDifficulty,
    val thumbnailUrl: String,
    val previewImages: List<String>,
    val isFavorite: Boolean
)

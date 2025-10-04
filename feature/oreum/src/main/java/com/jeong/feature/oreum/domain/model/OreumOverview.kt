package com.jeong.feature.oreum.domain.model

import com.jeong.domain.model.OreumDifficulty

data class OreumOverview(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val elevation: Double,
    val difficulty: OreumDifficulty,
    val thumbnailUrl: String,
    val previewImages: List<String>,
    val isFavorite: Boolean
)

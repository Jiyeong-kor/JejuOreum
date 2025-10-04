package com.jeong.feature.oreum.presentation.model

import com.jeong.domain.model.Oreum
import com.jeong.domain.model.OreumDifficulty

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

fun Oreum.toUiModel(): OreumUiModel = OreumUiModel(
    id = id,
    name = name,
    location = location,
    description = description,
    elevation = "${elevation.toInt()} m",
    difficulty = difficulty,
    thumbnailUrl = thumbnailUrl,
    previewImages = imageUrls,
    isFavorite = isFavorite
)

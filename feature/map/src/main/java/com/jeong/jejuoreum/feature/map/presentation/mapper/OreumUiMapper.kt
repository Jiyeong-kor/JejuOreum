package com.jeong.jejuoreum.feature.oreum.presentation.mapper

import com.jeong.jejuoreum.feature.oreum.domain.model.OreumOverview
import com.jeong.jejuoreum.feature.oreum.presentation.model.OreumUiModel
import javax.inject.Inject

class OreumUiMapper @Inject constructor() {

    fun map(overview: OreumOverview): OreumUiModel = OreumUiModel(
        id = overview.id,
        name = overview.name,
        location = overview.location,
        description = overview.description,
        elevation = formatElevation(overview.elevation),
        difficulty = overview.difficulty,
        thumbnailUrl = overview.thumbnailUrl,
        previewImages = overview.previewImages,
        isFavorite = overview.isFavorite
    )

    private fun formatElevation(elevation: Double): String = "${elevation.toInt()} m"
}

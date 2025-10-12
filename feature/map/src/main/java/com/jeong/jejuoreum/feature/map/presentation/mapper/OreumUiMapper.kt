package com.jeong.jejuoreum.feature.map.presentation.mapper

import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.OreumDifficulty
import com.jeong.jejuoreum.feature.map.presentation.model.OreumUiModel
import javax.inject.Inject

class OreumUiMapper @Inject constructor() {

    fun map(summary: ResultSummary): OreumUiModel = OreumUiModel(
        id = summary.idx.toString(),
        name = summary.oreumKname.ifBlank { summary.oreumEname },
        location = summary.oreumAddr,
        description = summary.explain,
        elevationMeters = summary.oreumAltitu,
        difficulty = OreumDifficulty.MODERATE,
        thumbnailUrl = summary.imgPath,
        previewImages = listOf(summary.imgPath),
        isFavorite = summary.userLiked,
    )
}

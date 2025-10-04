package com.jeong.feature.oreum.domain.mapper

import com.jeong.domain.model.Oreum
import com.jeong.feature.oreum.domain.model.OreumOverview
import javax.inject.Inject

class OreumOverviewMapper @Inject constructor() {

    fun map(domain: Oreum): OreumOverview = with(domain) {
        OreumOverview(
            id = id,
            name = name,
            location = location,
            description = description,
            elevation = elevation,
            difficulty = difficulty,
            thumbnailUrl = thumbnailUrl,
            previewImages = imageUrls,
            isFavorite = isFavorite
        )
    }
}

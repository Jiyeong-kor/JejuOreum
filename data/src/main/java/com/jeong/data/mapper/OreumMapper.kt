package com.jeong.data.mapper

import com.jeong.data.model.OreumEntity
import com.jeong.data.model.OreumResponse
import com.jeong.domain.model.Oreum
import com.jeong.domain.model.OreumDifficulty

internal fun OreumResponse.toEntity(): OreumEntity = OreumEntity(
    id = id,
    name = name,
    location = location,
    description = description,
    elevation = elevation,
    difficulty = difficulty,
    thumbnailUrl = thumbnailUrl,
    imageUrls = images,
    isFavorite = isFavorite
)

internal fun OreumEntity.toDomain(): Oreum = Oreum(
    id = id,
    name = name,
    location = location,
    description = description,
    elevation = elevation,
    difficulty = OreumDifficulty.entries.firstOrNull {
        it.name.equals(
            difficulty,
            ignoreCase = true
        )
    }
        ?: OreumDifficulty.MODERATE,
    thumbnailUrl = thumbnailUrl,
    imageUrls = imageUrls,
    isFavorite = isFavorite
)

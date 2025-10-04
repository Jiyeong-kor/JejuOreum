package com.jeong.data.mapper

import com.jeong.data.model.OreumEntity
import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.model.Oreum
import com.jeong.domain.model.OreumDifficulty

internal fun ResultSummary.toEntity(): OreumEntity = OreumEntity(
    idx = idx,
    oreumEname = oreumEname,
    oreumKname = oreumKname,
    oreumAddr = oreumAddr,
    oreumAltitu = oreumAltitu,
    x = x,
    y = y,
    explain = explain,
    imgPath = imgPath,
    totalFavorites = totalFavorites,
    totalStamps = totalStamps,
    userLiked = userLiked,
    userStamped = userStamped
)

internal fun OreumEntity.toDomainSummary(): ResultSummary = ResultSummary(
    idx = idx,
    oreumEname = oreumEname,
    oreumKname = oreumKname,
    oreumAddr = oreumAddr,
    oreumAltitu = oreumAltitu,
    x = x,
    y = y,
    explain = explain,
    imgPath = imgPath,
    totalFavorites = totalFavorites,
    totalStamps = totalStamps,
    userLiked = userLiked,
    userStamped = userStamped
)

internal fun ResultSummary.toDomainOreum(): Oreum = Oreum(
    id = idx.toString(),
    name = oreumKname.ifBlank { oreumEname },
    location = oreumAddr,
    description = explain,
    elevation = oreumAltitu,
    difficulty = OreumDifficulty.MODERATE,
    thumbnailUrl = imgPath,
    imageUrls = listOf(imgPath),
    isFavorite = userLiked
)

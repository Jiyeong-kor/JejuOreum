package com.jeong.jejuoreum.data.oreum.mapper

import com.jeong.jejuoreum.data.oreum.model.OreumEntity
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.model.OreumDifficulty

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

package com.jeong.jejuoreum.feature.detail.presentation.model

import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary

fun ResultSummary.toUiModel(): OreumSummaryUiModel = OreumSummaryUiModel(
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
    userStamped = userStamped,
)

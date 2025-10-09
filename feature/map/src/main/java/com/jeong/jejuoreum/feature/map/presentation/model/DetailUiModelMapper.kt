package com.jeong.jejuoreum.feature.map.presentation.model

import com.jeong.jejuoreum.feature.detail.presentation.model.OreumSummaryUiModel as DetailOreumSummaryUiModel

fun OreumSummaryUiModel.toDetailUiModel(): DetailOreumSummaryUiModel = DetailOreumSummaryUiModel(
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

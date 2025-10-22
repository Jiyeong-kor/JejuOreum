package com.jeong.jejuoreum.feature.profile.presentation.favorite

import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject

class MyFavoriteUiMapper @Inject constructor() {

    fun mapToUiModels(summaries: List<ResultSummary>): List<OreumSummaryUiModel> =
        summaries.map(::mapToUiModel)

    private fun mapToUiModel(summary: ResultSummary): OreumSummaryUiModel =
        OreumSummaryUiModel(
            idx = summary.idx,
            oreumEname = summary.oreumEname,
            oreumKname = summary.oreumKname,
            oreumAddr = summary.oreumAddr,
            oreumAltitu = summary.oreumAltitu,
            x = summary.x,
            y = summary.y,
            explain = summary.explain,
            imgPath = summary.imgPath,
            totalFavorites = summary.totalFavorites,
            totalStamps = summary.totalStamps,
            userLiked = summary.userLiked,
            userStamped = summary.userStamped,
        )
}

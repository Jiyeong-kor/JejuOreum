package com.jeong.jejuoreum.feature.map.presentation.model

import android.os.Parcelable
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import kotlinx.parcelize.Parcelize

@Parcelize
data class OreumSummaryUiModel(
    val idx: Int = -1,
    val oreumEname: String = "",
    val oreumKname: String = "",
    val oreumAddr: String = "",
    val oreumAltitu: Double = 0.0,
    val x: Double = 0.0,
    val y: Double = 0.0,
    val explain: String = "",
    val imgPath: String = "",
    val totalFavorites: Int = 0,
    val totalStamps: Int = 0,
    val userLiked: Boolean = false,
    val userStamped: Boolean = false
) : Parcelable

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
    userStamped = userStamped
)

fun OreumSummaryUiModel.toDomain(): ResultSummary = ResultSummary(
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

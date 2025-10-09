package com.jeong.jejuoreum.feature.detail.domain.model

data class OreumStampRequest(
    val oreumIdx: String,
    val oreumName: String,
    val latitude: Double,
    val longitude: Double
)

package com.jeong.jejuoreum.feature.oreum.domain.model

data class OreumStampRequest(
    val oreumIdx: String,
    val oreumName: String,
    val latitude: Double,
    val longitude: Double
)

package com.jeong.jejuoreum.domain.entity

data class ResultSummary(
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
)

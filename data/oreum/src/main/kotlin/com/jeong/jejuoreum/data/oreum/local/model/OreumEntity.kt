package com.jeong.jejuoreum.data.oreum.local.model

data class OreumEntity(
    val idx: Int,
    val oreumEname: String,
    val oreumKname: String,
    val oreumAddr: String,
    val oreumAltitu: Double,
    val x: Double,
    val y: Double,
    val explain: String,
    val imgPath: String,
    val totalFavorites: Int,
    val totalStamps: Int,
    val userLiked: Boolean,
    val userStamped: Boolean
)

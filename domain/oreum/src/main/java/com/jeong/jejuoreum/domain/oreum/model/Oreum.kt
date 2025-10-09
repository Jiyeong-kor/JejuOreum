package com.jeong.jejuoreum.domain.oreum.model

data class Oreum(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val elevation: Double,
    val difficulty: OreumDifficulty,
    val thumbnailUrl: String,
    val imageUrls: List<String>,
    val isFavorite: Boolean
)

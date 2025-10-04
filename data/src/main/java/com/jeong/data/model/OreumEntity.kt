package com.jeong.data.model

data class OreumEntity(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val elevation: Double,
    val difficulty: String,
    val thumbnailUrl: String,
    val imageUrls: List<String>,
    val isFavorite: Boolean
)

package com.jeong.data.model

data class OreumResponse(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val elevation: Double,
    val difficulty: String,
    val thumbnailUrl: String,
    val images: List<String>,
    val isFavorite: Boolean
)

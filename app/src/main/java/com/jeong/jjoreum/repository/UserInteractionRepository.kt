package com.jeong.jjoreum.repository

interface UserInteractionRepository {
    suspend fun getFavoriteStatus(oreumIdx: String): Boolean
    suspend fun getStampStatus(oreumIdx: String): Boolean
    suspend fun toggleFavorite(oreumIdx: String, newIsFavorite: Boolean): Int
    suspend fun getAllFavoriteStatus(): Map<String, Boolean>
    suspend fun getAllStampStatus(): Map<String, Boolean>
    suspend fun getCurrentUserNickname(): String
}
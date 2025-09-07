package com.jeong.domain.repository

interface StampRepository {
    suspend fun tryStamp(
        oreumIdx: String,
        oreumName: String,
        oreumLat: Double,
        oreumLng: Double
    ): Result<Unit>
}

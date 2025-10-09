package com.jeong.jejuoreum.domain.repository

interface StampRepository {
    suspend fun tryStamp(
        oreumIdx: String,
        oreumName: String,
        oreumLat: Double,
        oreumLng: Double
    ): Result<Unit>
}

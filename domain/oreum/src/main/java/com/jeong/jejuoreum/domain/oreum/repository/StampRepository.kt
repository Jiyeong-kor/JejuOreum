package com.jeong.jejuoreum.domain.oreum.repository

interface StampRepository {
    suspend fun tryStamp(
        oreumIdx: String,
        oreumName: String,
        oreumLat: Double,
        oreumLng: Double
    ): Result<Unit>
}

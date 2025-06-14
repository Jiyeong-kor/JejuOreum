package com.jeong.jjoreum.repository

interface StampRepository {
    suspend fun tryStamp(
        oreumIdx: String,
        oreumName: String,
        oreumLat: Double,
        oreumLng: Double
    ): Result<Unit>
}

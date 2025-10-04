package com.jeong.data.datasource.remote

import com.jeong.data.model.OreumResponse
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class StubOreumRemoteDataSource @Inject constructor() : OreumRemoteDataSource {

    private val oreums = listOf(
        OreumResponse(
            id = "oreum-001",
            name = "Seongsan Ilchulbong",
            location = "Seogwipo-si",
            description = "A UNESCO World Heritage site formed by hydrovolcanic eruptions.",
            elevation = 182.0,
            difficulty = "EASY",
            thumbnailUrl = "https://example.com/images/oreum-001-thumb.jpg",
            images = listOf(
                "https://example.com/images/oreum-001-1.jpg",
                "https://example.com/images/oreum-001-2.jpg",
            ),
            isFavorite = true
        ),
        OreumResponse(
            id = "oreum-002",
            name = "Darangshi Oreum",
            location = "Jeju-si",
            description = "A parasitic cone with a crater lake and panoramic island views.",
            elevation = 382.0,
            difficulty = "MODERATE",
            thumbnailUrl = "https://example.com/images/oreum-002-thumb.jpg",
            images = listOf(
                "https://example.com/images/oreum-002-1.jpg",
                "https://example.com/images/oreum-002-2.jpg",
            ),
            isFavorite = false
        ),
        OreumResponse(
            id = "oreum-003",
            name = "Geomun Oreum",
            location = "Jeju-si",
            description = "A lava tube system surrounded by dense cedar forests.",
            elevation = 456.0,
            difficulty = "HARD",
            thumbnailUrl = "https://example.com/images/oreum-003-thumb.jpg",
            images = listOf(
                "https://example.com/images/oreum-003-1.jpg",
                "https://example.com/images/oreum-003-2.jpg",
            ),
            isFavorite = false
        )
    )

    override suspend fun fetchOreums(): List<OreumResponse> =
        withContext(Dispatchers.Default) {
            delay(250) // Simulate network latency
            oreums
        }

    override suspend fun fetchOreum(id: String): OreumResponse? =
        withContext(Dispatchers.Default) {
            delay(150)
            oreums.firstOrNull { it.id == id }
        }
}

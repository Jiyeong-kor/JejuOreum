package com.jeong.jejuoreum.data.oreum.remote.source

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Singleton
class StubOreumRemoteDataSource @Inject constructor(
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val imageUrlResolver: StubOreumImageUrlResolver,
) : OreumRemoteDataSource {

    private val oreums: Map<String, ResultSummary> by lazy {
        STUB_SUMMARIES
            .map { it.toResultSummary(imageUrlResolver::resolve) }
            .associateBy { it.idx.toString() }
    }

    override suspend fun fetchOreums(): List<ResultSummary> =
        withContext(dispatcherProvider.computation) {
            delay(250)
            oreums.values.map { it.copy() }
        }

    override suspend fun fetchOreum(id: String): ResultSummary? =
        withContext(dispatcherProvider.computation) {
            delay(150)
            oreums[id]?.copy()
        }
}

private data class StubOreumSummary(
    val idx: Int,
    val englishName: String,
    val koreanName: String,
    val address: String,
    val altitudeMeters: Double,
    val longitude: Double,
    val latitude: Double,
    val description: String,
    val imagePath: String,
    val totalFavorites: Int,
    val totalStamps: Int,
    val userLiked: Boolean,
    val userStamped: Boolean,
) {
    fun toResultSummary(imageResolver: (String) -> String): ResultSummary = ResultSummary(
        idx = idx,
        oreumEname = englishName,
        oreumKname = koreanName,
        oreumAddr = address,
        oreumAltitu = altitudeMeters,
        x = longitude,
        y = latitude,
        explain = description,
        imgPath = imageResolver(imagePath),
        totalFavorites = totalFavorites,
        totalStamps = totalStamps,
        userLiked = userLiked,
        userStamped = userStamped,
    )
}

private val STUB_SUMMARIES = listOf(
    StubOreumSummary(
        idx = 1,
        englishName = "Seongsan Ilchulbong",
        koreanName = "성산일출봉",
        address = "제주특별자치도 서귀포시 성산읍",
        altitudeMeters = 182.0,
        longitude = 126.941906,
        latitude = 33.459045,
        description = "A UNESCO World Heritage tuff cone famous for sunrise views.",
        imagePath = "images/oreum-001-thumb.jpg",
        totalFavorites = 1240,
        totalStamps = 845,
        userLiked = true,
        userStamped = false,
    ),
    StubOreumSummary(
        idx = 2,
        englishName = "Darangshi Oreum",
        koreanName = "다랑쉬오름",
        address = "제주특별자치도 제주시 구좌읍",
        altitudeMeters = 382.0,
        longitude = 126.833648,
        latitude = 33.478212,
        description = "Parasitic cone with crater lake and panoramic views of the island.",
        imagePath = "images/oreum-002-thumb.jpg",
        totalFavorites = 980,
        totalStamps = 623,
        userLiked = false,
        userStamped = false,
    ),
    StubOreumSummary(
        idx = 3,
        englishName = "Geomun Oreum",
        koreanName = "거문오름",
        address = "제주특별자치도 제주시 조천읍",
        altitudeMeters = 456.0,
        longitude = 126.716263,
        latitude = 33.524006,
        description = "Lava tube system surrounded by dense cedar forests and hiking trails.",
        imagePath = "images/oreum-003-thumb.jpg",
        totalFavorites = 1575,
        totalStamps = 1011,
        userLiked = false,
        userStamped = false,
    ),
)

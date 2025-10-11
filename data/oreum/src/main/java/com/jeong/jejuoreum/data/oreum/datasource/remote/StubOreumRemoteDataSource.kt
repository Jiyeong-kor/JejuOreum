package com.jeong.jejuoreum.data.oreum.datasource.remote

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Singleton
class StubOreumRemoteDataSource @Inject constructor(
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : OreumRemoteDataSource {

    private val oreums: Map<String, ResultSummary> = listOf(
        ResultSummary(
            idx = 1,
            oreumEname = "Seongsan Ilchulbong",
            oreumKname = "성산일출봉",
            oreumAddr = "제주특별자치도 서귀포시 성산읍",
            oreumAltitu = 182.0,
            x = 126.941906,
            y = 33.459045,
            explain = "A UNESCO World Heritage tuff cone famous for sunrise views.",
            imgPath = "https://example.com/images/oreum-001-thumb.jpg",
            totalFavorites = 1240,
            totalStamps = 845,
            userLiked = true,
            userStamped = false,
        ),
        ResultSummary(
            idx = 2,
            oreumEname = "Darangshi Oreum",
            oreumKname = "다랑쉬오름",
            oreumAddr = "제주특별자치도 제주시 구좌읍",
            oreumAltitu = 382.0,
            x = 126.833648,
            y = 33.478212,
            explain = "Parasitic cone with crater lake and panoramic views of the island.",
            imgPath = "https://example.com/images/oreum-002-thumb.jpg",
            totalFavorites = 980,
            totalStamps = 623,
            userLiked = false,
            userStamped = false
        ),
        ResultSummary(
            idx = 3,
            oreumEname = "Geomun Oreum",
            oreumKname = "거문오름",
            oreumAddr = "제주특별자치도 제주시 조천읍",
            oreumAltitu = 456.0,
            x = 126.716263,
            y = 33.524006,
            explain = "Lava tube system surrounded by dense cedar forests and hiking trails.",
            imgPath = "https://example.com/images/oreum-003-thumb.jpg",
            totalFavorites = 1575,
            totalStamps = 1011,
            userLiked = false,
            userStamped = false
        ),
    ).associateBy { it.idx.toString() }

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

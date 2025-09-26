package com.jeong.feature.oreum.domain.usecase

import com.jeong.domain.entity.GeoBounds
import com.jeong.domain.entity.ResultSummary
import javax.inject.Inject

class FilterOreumsWithinBoundsUseCase @Inject constructor() {
    operator fun invoke(
        oreums: List<ResultSummary>,
        bounds: GeoBounds
    ): List<ResultSummary> {
        val minLat = minOf(bounds.sw.lat, bounds.ne.lat)
        val maxLat = maxOf(bounds.sw.lat, bounds.ne.lat)
        val minLon = minOf(bounds.sw.lon, bounds.ne.lon)
        val maxLon = maxOf(bounds.sw.lon, bounds.ne.lon)

        return oreums.filter { oreum ->
            oreum.y in minLat..maxLat && oreum.x in minLon..maxLon
        }
    }
}

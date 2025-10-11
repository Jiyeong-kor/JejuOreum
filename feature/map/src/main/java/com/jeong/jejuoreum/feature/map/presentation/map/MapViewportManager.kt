package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.domain.oreum.entity.GeoBounds
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.entity.quantized
import com.jeong.jejuoreum.domain.oreum.usecase.FilterOreumsWithinBoundsUseCase
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class MapViewportManager @Inject constructor(
    private val filterOreumsWithinBoundsUseCase: FilterOreumsWithinBoundsUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {

    private val pinCache = mutableMapOf<GeoPoint, MapPinUiModel>()
    private var lastBounds: GeoBounds? = null

    suspend fun calculatePins(
        bounds: GeoBounds,
        summaries: List<ResultSummary>,
    ): MapViewportResult? {
        if (summaries.isEmpty()) {
            lastBounds = bounds
            return MapViewportResult(bounds, emptyList())
        }
        if (bounds == lastBounds) return null
        lastBounds = bounds
        return withContext(dispatcherProvider.computation) {
            val visible = filterOreumsWithinBoundsUseCase(summaries, bounds)
            val pins = visible.map { summary ->
                val point = GeoPoint(summary.y, summary.x).quantized()
                pinCache.getOrPut(point) {
                    MapPinUiModel(summary.oreumKname, summary.y, summary.x)
                }
            }
            MapViewportResult(bounds, pins)
        }
    }

    fun reset() {
        lastBounds = null
        pinCache.clear()
    }
}

internal data class MapViewportResult(
    val bounds: GeoBounds,
    val pins: List<MapPinUiModel>,
)

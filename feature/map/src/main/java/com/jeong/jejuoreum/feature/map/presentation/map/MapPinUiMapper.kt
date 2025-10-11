package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.entity.quantized
import javax.inject.Inject

internal class MapPinUiMapper @Inject constructor() {

    private val pinCache = mutableMapOf<GeoPoint, MapPinUiModel>()

    fun mapAll(summaries: List<ResultSummary>): List<MapPinUiModel> =
        summaries.map { map(it) }

    fun clear() {
        pinCache.clear()
    }

    private fun map(summary: ResultSummary): MapPinUiModel {
        val quantized = GeoPoint(summary.y, summary.x).quantized()
        return pinCache.getOrPut(quantized) {
            MapPinUiModel(
                title = summary.oreumKname,
                lat = summary.y,
                lon = summary.x,
            )
        }
    }
}

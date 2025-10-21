package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.core.common.state.StateReducer
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.feature.map.presentation.model.OreumSummaryUiMapper
import javax.inject.Inject

internal class MapViewStateReducer @Inject constructor(
    private val summaryUiMapper: OreumSummaryUiMapper,
    private val mapPinUiMapper: MapPinUiMapper,
) : StateReducer<MapViewState, MapViewChange> {

    override fun reduce(current: MapViewState, change: MapViewChange): MapViewState =
        when (change) {
            is MapViewChange.VisiblePins -> updatePins(current, change)
            is MapViewChange.Selection -> current.copy(
                selectedOreum = change.summary?.let(summaryUiMapper::map),
            )

            MapViewChange.ClearSelection ->
                if (current.selectedOreum == null) current else current.copy(selectedOreum = null)

            is MapViewChange.CameraSaved ->
                if (current.cameraSnapshot == change.snapshot) current
                else current.copy(cameraSnapshot = change.snapshot)
        }

    private fun updatePins(current: MapViewState, change: MapViewChange.VisiblePins): MapViewState {
        if (change.forceRefresh) {
            mapPinUiMapper.clear()
        }

        val mappedPins = mapPinUiMapper.mapAll(change.visibleSummaries)
        return if (mappedPins == current.visiblePins) {
            current
        } else {
            current.copy(visiblePins = mappedPins)
        }
    }
}

internal sealed interface MapViewChange {
    data class VisiblePins(
        val visibleSummaries: List<ResultSummary>,
        val forceRefresh: Boolean,
    ) : MapViewChange

    data class Selection(val summary: ResultSummary?) : MapViewChange
    data object ClearSelection : MapViewChange
    data class CameraSaved(val snapshot: CameraSnapshot) : MapViewChange
}

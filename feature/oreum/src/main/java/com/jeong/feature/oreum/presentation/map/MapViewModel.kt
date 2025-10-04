package com.jeong.feature.oreum.presentation.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jeong.core.ui.viewmodel.BaseViewModel
import com.jeong.core.utils.coroutines.CoroutineDispatcherProvider
import com.jeong.domain.entity.GeoBounds
import com.jeong.domain.entity.GeoPoint
import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.entity.quantized
import com.jeong.domain.usecase.oreum.FilterOreumsWithinBoundsUseCase
import com.jeong.domain.usecase.oreum.FindOreumByLocationUseCase
import com.jeong.domain.usecase.oreum.ObserveOreumSummariesUseCase
import com.jeong.domain.usecase.oreum.SearchOreumsUseCase
import com.jeong.feature.oreum.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val KEY_CAM_LAT = "cam_lat"
private const val KEY_CAM_LON = "cam_lon"
private const val KEY_CAM_ZOOM = "cam_zoom"

@HiltViewModel
class MapViewModel @Inject constructor(
    observeOreumSummariesUseCase: ObserveOreumSummariesUseCase,
    private val filterOreumsWithinBoundsUseCase: FilterOreumsWithinBoundsUseCase,
    private val searchOreumsUseCase: SearchOreumsUseCase,
    private val findOreumByLocationUseCase: FindOreumByLocationUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : BaseViewModel<MapEvent, MapEffect, MapUiState>(
    initialState = MapUiState(cameraSnapshot = restoreCameraFromSavedState(savedStateHandle))
) {

    private val oreumList: StateFlow<List<ResultSummary>> = observeOreumSummariesUseCase()

    private val pinCache = mutableMapOf<GeoPoint, MapPinUi>()
    private var lastBounds: GeoBounds? = null
    private var searchJob: Job? = null

    override fun handleEvent(event: MapEvent) {
        when (event) {
            is MapEvent.SearchQueryChanged -> handleSearchQuery(event.query)
            is MapEvent.ViewportUpdated -> handleViewport(event.bounds)
            is MapEvent.MarkerSelected -> handleMarkerSelection(event.point)
            MapEvent.SelectionCleared -> clearSelection()
            MapEvent.SearchPanelClosed -> closeSearchPanel()
            is MapEvent.CameraSaved -> persistCamera(event.center, event.zoomLevel)
        }
    }

    private fun handleSearchQuery(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            searchJob = null
            setState {
                copy(
                    searchQuery = "",
                    panelState = MapPanelState.Hidden,
                    searchResults = emptyList(),
                )
            }
            return
        }
        setState { copy(searchQuery = query) }
        searchJob = viewModelScope.launch(dispatcherProvider.computation) {
            try {
                val results = searchOreumsUseCase(oreumList.value, query)
                    .map { it.toUiModel() }
                val panelState = if (results.isEmpty()) {
                    MapPanelState.NoResults
                } else {
                    MapPanelState.Results
                }
                withContext(dispatcherProvider.main) {
                    setState {
                        copy(
                            searchQuery = query,
                            searchResults = results,
                            panelState = panelState,
                        )
                    }
                }
            } finally {
                searchJob = null
            }
        }
    }

    private fun handleViewport(bounds: GeoBounds) {
        if (bounds == lastBounds) return
        lastBounds = bounds
        viewModelScope.launch(dispatcherProvider.computation) {
            val visible = filterOreumsWithinBoundsUseCase(oreumList.value, bounds)
            val pins = visible.map { summary ->
                val point = GeoPoint(summary.y, summary.x).quantized()
                pinCache.getOrPut(point) {
                    MapPinUi(summary.oreumKname, summary.y, summary.x)
                }
            }
            if (pins != state.value.visiblePins) {
                withContext(dispatcherProvider.main) {
                    setState { copy(visiblePins = pins) }
                }
            }
        }
    }

    private fun handleMarkerSelection(point: GeoPoint) {
        viewModelScope.launch(dispatcherProvider.computation) {
            val oreum = findOreumByLocationUseCase(oreumList.value, point)
            val uiModel = oreum?.toUiModel()
            withContext(dispatcherProvider.main) {
                setState { copy(selectedOreum = uiModel) }
            }
        }
    }

    private fun clearSelection() {
        setState { copy(selectedOreum = null) }
    }

    private fun closeSearchPanel() {
        searchJob?.cancel()
        searchJob = null
        setState {
            copy(
                searchQuery = "",
                panelState = MapPanelState.Hidden,
                searchResults = emptyList(),
            )
        }
    }

    private fun persistCamera(center: GeoPoint, zoomLevel: Int) {
        setState { copy(cameraSnapshot = CameraSnapshot(center, zoomLevel)) }
        savedStateHandle[KEY_CAM_LAT] = center.lat
        savedStateHandle[KEY_CAM_LON] = center.lon
        savedStateHandle[KEY_CAM_ZOOM] = zoomLevel
    }

    companion object {
        private fun restoreCameraFromSavedState(savedStateHandle: SavedStateHandle): CameraSnapshot? {
            val lat = savedStateHandle.get<Double>(KEY_CAM_LAT)
            val lon = savedStateHandle.get<Double>(KEY_CAM_LON)
            val zoom = savedStateHandle.get<Int>(KEY_CAM_ZOOM)
            return if (lat != null && lon != null && zoom != null) {
                CameraSnapshot(GeoPoint(lat, lon), zoom)
            } else {
                null
            }
        }
    }
}

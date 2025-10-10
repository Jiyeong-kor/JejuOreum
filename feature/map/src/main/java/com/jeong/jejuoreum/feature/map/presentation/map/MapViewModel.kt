package com.jeong.jejuoreum.feature.map.presentation.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.domain.oreum.entity.GeoBounds
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.entity.quantized
import com.jeong.jejuoreum.domain.oreum.usecase.FilterOreumsWithinBoundsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.FindOreumByLocationUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.SearchOreumsUseCase
import com.jeong.jejuoreum.core.ui.viewmodel.BaseViewModel
import com.jeong.jejuoreum.feature.map.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val KEY_CAM_LAT = "cam_lat"
private const val KEY_CAM_LON = "cam_lon"
private const val KEY_CAM_ZOOM = "cam_zoom"

@HiltViewModel
class MapViewModel @Inject constructor(
    private val observeOreumSummariesUseCase: ObserveOreumSummariesUseCase,
    private val filterOreumsWithinBoundsUseCase: FilterOreumsWithinBoundsUseCase,
    private val searchOreumsUseCase: SearchOreumsUseCase,
    private val findOreumByLocationUseCase: FindOreumByLocationUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : BaseViewModel<MapEvent, MapEffect, MapUiState>(
    initialState = MapUiState(cameraSnapshot = restoreCameraFromSavedState(savedStateHandle))
) {

    private val oreumSummaries = MutableStateFlow<List<ResultSummary>>(emptyList())

    private val pinCache = mutableMapOf<GeoPoint, MapPinUi>()
    private var lastBounds: GeoBounds? = null
    private var searchJob: Job? = null

    init {
        observeOreums()
    }

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
                val results = searchOreumsUseCase(oreumSummaries.value, query)
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
            val visible = filterOreumsWithinBoundsUseCase(oreumSummaries.value, bounds)
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
            val oreum = findOreumByLocationUseCase(oreumSummaries.value, point)
            val uiModel = oreum?.toUiModel()
            withContext(dispatcherProvider.main) {
                setState { copy(selectedOreum = uiModel) }
            }
        }
    }

    private fun observeOreums() {
        viewModelScope.launch {
            observeOreumSummariesUseCase().collectLatest { resource ->
                when (resource) {
                    Resource.Loading -> setState {
                        copy(isLoading = true, errorMessage = null)
                    }

                    is Resource.Success -> {
                        oreumSummaries.value = resource.data
                        setState { copy(isLoading = false, errorMessage = null) }
                        val currentQuery = state.value.searchQuery
                        if (currentQuery.isNotBlank()) {
                            handleSearchQuery(currentQuery)
                        }
                        lastBounds?.let { bounds ->
                            lastBounds = null
                            handleViewport(bounds)
                        }
                    }

                    is Resource.Error -> {
                        val message = resource.throwable?.message
                            ?: "오름 데이터를 불러오지 못했어요."
                        setState { copy(isLoading = false, errorMessage = message) }
                        sendEffect { MapEffect.ShowMessage(message) }
                    }
                }
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

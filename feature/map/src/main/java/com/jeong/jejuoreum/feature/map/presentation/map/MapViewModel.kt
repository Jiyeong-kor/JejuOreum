package com.jeong.jejuoreum.feature.map.presentation.map

import androidx.lifecycle.SavedStateHandle
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.common.result.ResourceError
import com.jeong.jejuoreum.core.common.UiText
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.presentation.CommonBaseViewModel
import com.jeong.jejuoreum.domain.oreum.entity.GeoBounds
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveOreumSummariesUseCase
import com.jeong.jejuoreum.feature.map.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class MapViewModel @Inject constructor(
    private val observeOreumSummariesUseCase: ObserveOreumSummariesUseCase,
    private val searchHandler: MapSearchHandler,
    private val viewportManager: MapViewportManager,
    private val selectionHandler: MapSelectionHandler,
    private val savedStateHandle: SavedStateHandle,
    @Named("ioDispatcher") ioDispatcher: CoroutineDispatcher,
) : CommonBaseViewModel<MapUiState, MapEvent, MapEffect>(ioDispatcher) {

    private val oreumSummaries = MutableStateFlow<List<ResultSummary>>(emptyList())
    private var currentViewportBounds: GeoBounds? = null
    private var searchJob: Job? = null

    init {
        observeOreums()
    }

    override fun initialState(): MapUiState = MapUiState(
        cameraSnapshot = restoreCameraFromSavedState(savedStateHandle)
    )

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

    override fun buildErrorEffect(message: String): MapEffect =
        MapEffect.ShowMessage(UiText.DynamicString(message))

    private fun handleSearchQuery(query: String) {
        searchJob?.cancel()
        val sanitized = query.trim()
        if (sanitized.isEmpty()) {
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
        setState { copy(searchQuery = sanitized) }
        searchJob = launch {
            try {
                val result = searchHandler.search(sanitized, oreumSummaries.value)
                setState {
                    copy(
                        searchQuery = sanitized,
                        searchResults = result.results,
                        panelState = result.panelState,
                    )
                }
            } finally {
                searchJob = null
            }
        }
    }

    private fun handleViewport(bounds: GeoBounds) {
        currentViewportBounds = bounds
        launch {
            val result = viewportManager.calculatePins(bounds, oreumSummaries.value) ?: return@launch
            if (result.pins != currentState.visiblePins) {
                setState { copy(visiblePins = result.pins) }
            }
        }
    }

    private fun handleMarkerSelection(point: GeoPoint) {
        launch {
            val uiModel = selectionHandler.resolveSelection(point, oreumSummaries.value)
            setState { copy(selectedOreum = uiModel) }
        }
    }

    private fun observeOreums() {
        launch {
            observeOreumSummariesUseCase().collectLatest { resource ->
                when (resource) {
                    Resource.Loading -> setState {
                        copy(isLoading = true, errorMessage = null)
                    }

                    is Resource.Success -> handleSummaries(resource.data)

                    is Resource.Error -> handleResourceError(resource.error)
                }
            }
        }
    }

    private fun handleSummaries(summaries: List<ResultSummary>) {
        oreumSummaries.value = summaries
        setState { copy(isLoading = false, errorMessage = null) }
        refreshSearchResults()
        refreshViewportPins()
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
        savedStateHandle[OreumNavigation.Map.SavedStateKeys.CAMERA_LATITUDE] = center.lat
        savedStateHandle[OreumNavigation.Map.SavedStateKeys.CAMERA_LONGITUDE] = center.lon
        savedStateHandle[OreumNavigation.Map.SavedStateKeys.CAMERA_ZOOM] = zoomLevel
    }

    private fun refreshSearchResults() {
        val currentQuery = currentState.searchQuery
        if (currentQuery.isNotBlank()) {
            handleSearchQuery(currentQuery)
        }
    }

    private fun refreshViewportPins() {
        viewportManager.reset()
        currentViewportBounds?.let { bounds -> handleViewport(bounds) }
    }

    private fun handleResourceError(error: ResourceError) {
        val message = when (error) {
            ResourceError.Network -> UiText.StringResource(R.string.error_network_unavailable)
            is ResourceError.Api -> error.message?.let(UiText::DynamicString) ?: defaultLoadErrorMessage()
            is ResourceError.NotFound -> UiText.StringResource(R.string.error_oreum_not_found)
            ResourceError.Unauthorized -> UiText.StringResource(R.string.error_authentication_required)
            is ResourceError.Unknown -> error.throwable.message?.let(UiText::DynamicString) ?: defaultLoadErrorMessage()
        }
        setState { copy(isLoading = false, errorMessage = message) }
        sendEffect { MapEffect.ShowMessage(message) }
    }

    private fun defaultLoadErrorMessage(): UiText =
        UiText.StringResource(R.string.error_failed_to_load_oreum_data)

    companion object {
        private fun restoreCameraFromSavedState(savedStateHandle: SavedStateHandle): CameraSnapshot? {
            val lat = savedStateHandle.get<Double>(OreumNavigation.Map.SavedStateKeys.CAMERA_LATITUDE)
            val lon = savedStateHandle.get<Double>(OreumNavigation.Map.SavedStateKeys.CAMERA_LONGITUDE)
            val zoom = savedStateHandle.get<Int>(OreumNavigation.Map.SavedStateKeys.CAMERA_ZOOM)
            return if (lat != null && lon != null && zoom != null) {
                CameraSnapshot(GeoPoint(lat, lon), zoom)
            } else {
                null
            }
        }
    }
}

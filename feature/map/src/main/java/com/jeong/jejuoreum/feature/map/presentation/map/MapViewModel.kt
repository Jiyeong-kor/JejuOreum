package com.jeong.jejuoreum.feature.map.presentation.map

import androidx.lifecycle.SavedStateHandle
import com.jeong.jejuoreum.core.common.UiText
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.common.result.ResourceError
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.presentation.CommonBaseViewModel
import com.jeong.jejuoreum.domain.oreum.entity.GeoBounds
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.SearchOreumsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.SearchOreumsUseCase.Result as SearchResult
import com.jeong.jejuoreum.domain.oreum.usecase.SelectOreumMarkerUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.UpdateMapViewportUseCase
import com.jeong.jejuoreum.feature.map.R
import com.jeong.jejuoreum.feature.map.presentation.map.MapEffect.ShowToast
import com.jeong.jejuoreum.feature.map.presentation.model.OreumSummaryUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getOreumSummariesUseCase: GetOreumSummariesUseCase,
    private val searchOreumsUseCase: SearchOreumsUseCase,
    private val updateMapViewportUseCase: UpdateMapViewportUseCase,
    private val selectOreumMarkerUseCase: SelectOreumMarkerUseCase,
    private val summaryUiMapper: OreumSummaryUiMapper,
    private val mapPinUiMapper: MapPinUiMapper,
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
        mapState = MapViewState(cameraSnapshot = restoreCameraFromSavedState(savedStateHandle))
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

    override fun buildErrorEffect(message: UiText): MapEffect = ShowToast(message)

    private fun handleSearchQuery(query: String) {
        searchJob?.cancel()
        val job = launch {
            when (val result = searchOreumsUseCase(oreumSummaries.value, query)) {
                SearchResult.EmptyQuery -> setState {
                    val updatedSearch = searchState.copy(
                        query = "",
                        panelState = MapPanelState.Hidden,
                        searchResults = emptyList(),
                    )
                    copy(searchState = updatedSearch)
                }

                is SearchResult.Matches -> setState {
                    val updatedSearch = searchState.copy(
                        query = result.sanitizedQuery,
                        searchResults = result.results.map(summaryUiMapper::map),
                        panelState = MapPanelState.Results,
                    )
                    copy(searchState = updatedSearch)
                }

                is SearchResult.NoMatches -> setState {
                    val updatedSearch = searchState.copy(
                        query = result.sanitizedQuery,
                        searchResults = emptyList(),
                        panelState = MapPanelState.NoResults,
                    )
                    copy(searchState = updatedSearch)
                }
            }
        }
        job.invokeOnCompletion { searchJob = null }
        searchJob = job
    }

    private fun handleViewport(bounds: GeoBounds, force: Boolean = false) {
        if (!force && bounds == currentViewportBounds) return
        currentViewportBounds = bounds
        launch {
            val result = updateMapViewportUseCase(oreumSummaries.value, bounds)
            val pins = mapPinUiMapper.mapAll(result.visibleSummaries)
            if (pins != currentState.mapState.visiblePins) {
                setState {
                    copy(mapState = mapState.copy(visiblePins = pins))
                }
            }
        }
    }

    private fun handleMarkerSelection(point: GeoPoint) {
        launch {
            val selected = selectOreumMarkerUseCase(oreumSummaries.value, point)
            setState {
                copy(
                    mapState = mapState.copy(
                        selectedOreum = selected?.let(summaryUiMapper::map)
                    )
                )
            }
        }
    }

    private fun observeOreums() {
        launch {
            getOreumSummariesUseCase().collectLatest { resource ->
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
        setState { copy(mapState = mapState.copy(selectedOreum = null)) }
    }

    private fun closeSearchPanel() {
        searchJob?.cancel()
        searchJob = null
        setState {
            val updatedSearch = searchState.copy(
                query = "",
                panelState = MapPanelState.Hidden,
                searchResults = emptyList(),
            )
            copy(searchState = updatedSearch)
        }
    }

    private fun persistCamera(center: GeoPoint, zoomLevel: Int) {
        setState {
            copy(mapState = mapState.copy(cameraSnapshot = CameraSnapshot(center, zoomLevel)))
        }
        savedStateHandle[OreumNavigation.Map.SavedStateKeys.CAMERA_LATITUDE] = center.lat
        savedStateHandle[OreumNavigation.Map.SavedStateKeys.CAMERA_LONGITUDE] = center.lon
        savedStateHandle[OreumNavigation.Map.SavedStateKeys.CAMERA_ZOOM] = zoomLevel
    }

    private fun refreshSearchResults() {
        val currentQuery = currentState.searchState.query
        if (currentQuery.isNotBlank()) {
            handleSearchQuery(currentQuery)
        }
    }

    private fun refreshViewportPins() {
        mapPinUiMapper.clear()
        currentViewportBounds?.let { bounds -> handleViewport(bounds, force = true) }
    }

    private fun handleResourceError(error: ResourceError) {
        val message = when (error) {
            ResourceError.Network -> UiText.StringResource(R.string.error_network_unavailable)
            is ResourceError.Api -> defaultLoadErrorMessage()
            is ResourceError.NotFound -> UiText.StringResource(R.string.error_oreum_not_found)
            ResourceError.Unauthorized -> UiText.StringResource(R.string.error_authentication_required)
            is ResourceError.Unknown -> defaultLoadErrorMessage()
        }
        setState { copy(isLoading = false, errorMessage = message) }
        sendEffect { ShowToast(message) }
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

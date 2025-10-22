package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.core.architecture.UiText
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.common.result.ResourceError
import com.jeong.jejuoreum.core.architecture.viewmodel.CommonBaseViewModel
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
    private val searchStateReducer: MapSearchStateReducer,
    private val mapViewStateReducer: MapViewStateReducer,
    private val cameraStateStorage: MapCameraStateStorage,
    @Named("ioDispatcher") ioDispatcher: CoroutineDispatcher,
) : CommonBaseViewModel<MapUiState, MapEvent, MapEffect>(ioDispatcher) {

    private val oreumSummaries = MutableStateFlow<List<ResultSummary>>(emptyList())
    private var currentViewportBounds: GeoBounds? = null
    private var searchJob: Job? = null

    init {
        observeOreums()
    }

    override fun initialState(): MapUiState = MapUiState(
        mapState = MapViewState(cameraSnapshot = cameraStateStorage.restore())
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
                SearchResult.EmptyQuery -> reduceSearchState(MapSearchChange.EmptyQuery)
                is SearchResult.Matches -> reduceSearchState(
                    MapSearchChange.Matches(result.sanitizedQuery, result.results)
                )

                is SearchResult.NoMatches -> reduceSearchState(
                    MapSearchChange.NoMatches(result.sanitizedQuery)
                )
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
            reduceMapState(MapViewChange.VisiblePins(result.visibleSummaries, force))
        }
    }

    private fun handleMarkerSelection(point: GeoPoint) {
        launch {
            val selected = selectOreumMarkerUseCase(oreumSummaries.value, point)
            reduceMapState(MapViewChange.Selection(selected))
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
        reduceMapState(MapViewChange.ClearSelection)
    }

    private fun closeSearchPanel() {
        searchJob?.cancel()
        searchJob = null
        reduceSearchState(MapSearchChange.PanelClosed)
    }

    private fun persistCamera(center: GeoPoint, zoomLevel: Int) {
        val snapshot = CameraSnapshot(center, zoomLevel)
        reduceMapState(MapViewChange.CameraSaved(snapshot))
        cameraStateStorage.persist(snapshot)
    }

    private fun refreshSearchResults() {
        val currentQuery = currentState.searchState.query
        if (currentQuery.isNotBlank()) {
            handleSearchQuery(currentQuery)
        }
    }

    private fun refreshViewportPins() {
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

    private fun reduceSearchState(change: MapSearchChange) {
        setState {
            val updatedSearch = searchStateReducer.reduce(searchState, change)
            if (updatedSearch == searchState) this else copy(searchState = updatedSearch)
        }
    }

    private fun reduceMapState(change: MapViewChange) {
        setState {
            val updatedMap = mapViewStateReducer.reduce(mapState, change)
            if (updatedMap == mapState) this else copy(mapState = updatedMap)
        }
    }
}

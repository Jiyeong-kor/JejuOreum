package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import com.jeong.jejuoreum.core.presentation.UiText
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.oreum.entity.GeoBounds
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint

data class MapUiState(
    val searchState: SearchState = SearchState(),
    val mapState: MapViewState = MapViewState(),
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
) : UiState {
    val isSearchPanelVisible: Boolean
        get() = searchState.panelState != MapPanelState.Hidden

    val isShowingResults: Boolean
        get() = searchState.panelState == MapPanelState.Results

    val isShowingEmptyResult: Boolean
        get() = searchState.panelState == MapPanelState.NoResults
}

data class SearchState(
    val query: String = "",
    val panelState: MapPanelState = MapPanelState.Hidden,
    val searchResults: List<OreumSummaryUiModel> = emptyList(),
)

data class MapViewState(
    val cameraSnapshot: CameraSnapshot? = null,
    val visiblePins: List<MapPinUiModel> = emptyList(),
    val selectedOreum: OreumSummaryUiModel? = null,
)

sealed interface MapPanelState {
    data object Hidden : MapPanelState
    data object Results : MapPanelState
    data object NoResults : MapPanelState
}

sealed interface MapEvent : UiEvent {
    data class SearchQueryChanged(val query: String) : MapEvent
    data class ViewportUpdated(val bounds: GeoBounds) : MapEvent
    data class MarkerSelected(val point: GeoPoint) : MapEvent
    data object SelectionCleared : MapEvent
    data object SearchPanelClosed : MapEvent
    data class CameraSaved(val center: GeoPoint, val zoomLevel: Int) : MapEvent
}

sealed interface MapEffect : UiEffect {
    data class ShowToast(val message: UiText) : MapEffect
}

data class CameraSnapshot(val center: GeoPoint, val zoomLevel: Int)

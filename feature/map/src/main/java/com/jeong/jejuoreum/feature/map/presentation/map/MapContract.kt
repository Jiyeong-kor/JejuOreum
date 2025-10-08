package com.jeong.feature.oreum.presentation.map

import com.jeong.core.ui.state.UiEffect
import com.jeong.core.ui.state.UiEvent
import com.jeong.core.ui.state.UiState
import com.jeong.domain.entity.GeoBounds
import com.jeong.domain.entity.GeoPoint
import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel

data class MapUiState(
    val searchQuery: String = "",
    val panelState: MapPanelState = MapPanelState.Hidden,
    val searchResults: List<OreumSummaryUiModel> = emptyList(),
    val visiblePins: List<MapPinUi> = emptyList(),
    val selectedOreum: OreumSummaryUiModel? = null,
    val cameraSnapshot: CameraSnapshot? = null
) : UiState {
    val isSearchPanelVisible: Boolean
        get() = panelState != MapPanelState.Hidden

    val isShowingResults: Boolean
        get() = panelState == MapPanelState.Results

    val isShowingEmptyResult: Boolean
        get() = panelState == MapPanelState.NoResults
}

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

sealed interface MapEffect : UiEffect

data class CameraSnapshot(val center: GeoPoint, val zoomLevel: Int)

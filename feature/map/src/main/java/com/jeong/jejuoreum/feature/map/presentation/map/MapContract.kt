package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.core.common.UiText
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.oreum.entity.GeoBounds
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint

data class MapUiState(
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val searchQuery: String = "",
    val panelState: MapPanelState = MapPanelState.Hidden,
    val searchResults: List<OreumSummaryUiModel> = emptyList(),
    val visiblePins: List<MapPinUiModel> = emptyList(),
    val selectedOreum: OreumSummaryUiModel? = null,
    val cameraSnapshot: CameraSnapshot? = null
) {
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

sealed interface MapEvent {
    data class SearchQueryChanged(val query: String) : MapEvent
    data class ViewportUpdated(val bounds: GeoBounds) : MapEvent
    data class MarkerSelected(val point: GeoPoint) : MapEvent
    data object SelectionCleared : MapEvent
    data object SearchPanelClosed : MapEvent
    data class CameraSaved(val center: GeoPoint, val zoomLevel: Int) : MapEvent
}

sealed interface MapEffect {
    data class ShowMessage(val message: UiText) : MapEffect
}

data class CameraSnapshot(val center: GeoPoint, val zoomLevel: Int)

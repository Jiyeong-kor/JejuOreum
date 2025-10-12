package com.jeong.jejuoreum.feature.map.presentation.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.feature.map.R

@Composable
internal fun MapScreen(
    uiState: MapUiState,
    onEvent: (MapEvent) -> Unit,
    onNavigateToDetail: (OreumSummaryUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var detailOverlay by remember { mutableStateOf(uiState.mapState.selectedOreum) }
    var mapController by remember { mutableStateOf<MapController?>(null) }

    BackHandler(enabled = uiState.isShowingResults || uiState.isShowingEmptyResult) {
        onEvent(MapEvent.SearchPanelClosed)
        focusManager.clearFocus()
    }

    LaunchedEffect(uiState.mapState.selectedOreum) {
        detailOverlay = uiState.mapState.selectedOreum
    }

    val horizontalPadding = dimensionResource(id = R.dimen.map_search_panel_padding_horizontal)
    val verticalPadding = dimensionResource(id = R.dimen.map_search_panel_padding_vertical)

    Box(modifier = modifier.fillMaxSize()) {
        MapViewHost(
            uiState = uiState,
            modifier = Modifier.fillMaxSize(),
            onEvent = onEvent,
            onMapReady = { controller -> mapController = controller },
            onMapTap = {
                detailOverlay = null
                onEvent(MapEvent.SelectionCleared)
                onEvent(MapEvent.SearchPanelClosed)
                focusManager.clearFocus()
            }
        )
        SearchPanel(
            query = uiState.searchState.query,
            state = uiState,
            onQueryChange = { query -> onEvent(MapEvent.SearchQueryChanged(query)) },
            onResultClick = { item ->
                mapController?.selectMarkerAt(item.asLatLng())
                mapController?.moveCameraTo(item.asLatLng())
                detailOverlay = item
                onEvent(MapEvent.MarkerSelected(item.asGeoPoint()))
                onEvent(MapEvent.SearchPanelClosed)
                focusManager.clearFocus()
            },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .displayCutoutPadding()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        )
        DetailSheet(
            overlay = detailOverlay,
            controller = mapController,
            onDismiss = {
                detailOverlay = null
                onEvent(MapEvent.SelectionCleared)
            },
            onNavigateToDetail = { oreum ->
                onNavigateToDetail(oreum)
                detailOverlay = null
            }
        )
        if (uiState.isLoading) {
            LoadingIndicatorOverlay(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun LoadingIndicatorOverlay(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

private fun OreumSummaryUiModel.asLatLng(): com.kakao.vectormap.LatLng =
    com.kakao.vectormap.LatLng.from(y, x)

private fun OreumSummaryUiModel.asGeoPoint(): GeoPoint = GeoPoint(y, x)

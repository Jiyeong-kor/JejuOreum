package com.jeong.jejuoreum.feature.oreum.presentation.map

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jejuoreum.domain.entity.GeoPoint
import com.jeong.jejuoreum.feature.oreum.presentation.detail.DetailViewModel
import com.jeong.jejuoreum.feature.oreum.presentation.model.OreumSummaryUiModel

private val T.searchQuery: Any

private tailrec fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun MapRoute(
    onNavigateToWriteReview: (Int, String) -> Unit,
    onFavoriteToggled: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    requireNotNull(activity)

    val mapViewModel: MapViewModel = hiltViewModel(activity)
    val detailViewModel: DetailViewModel = hiltViewModel(activity)

    val mapState by mapViewModel.state.collectAsStateWithLifecycle()
    val selectedOreum = mapState.selectedOreum
    val query = mapState.searchQuery

    val focus = LocalFocusManager.current

    var detailOverlay by remember { mutableStateOf<OreumSummaryUiModel?>(null) }
    var controller by remember { mutableStateOf<MapController?>(null) }

    BackHandler(enabled = mapState.isShowingResults || mapState.isShowingEmptyResult) {
        mapViewModel.onEvent(MapEvent.SearchPanelClosed)
        focus.clearFocus()
    }

    LaunchedEffect(selectedOreum) { selectedOreum?.let { detailOverlay = it } }

    Box(modifier.fillMaxSize()) {
        MapViewHost(
            viewModel = mapViewModel,
            modifier = Modifier.fillMaxSize(),
            onMapReady = { controller = it },
            onMapTap = {
                detailOverlay = null
                mapViewModel.onEvent(MapEvent.SelectionCleared)
                mapViewModel.onEvent(MapEvent.SearchPanelClosed)
                focus.clearFocus()
            }
        )
        SearchPanel(
            query = query,
            state = mapState,
            onQueryChange = { q -> mapViewModel.onEvent(MapEvent.SearchQueryChanged(q)) },
            onResultClick = { item ->
                controller?.selectMarkerAt(item.asLatLng())
                controller?.moveCameraTo(item.asLatLng())
                detailOverlay = item
                mapViewModel.onEvent(MapEvent.MarkerSelected(item.asGeoPoint()))
                mapViewModel.onEvent(MapEvent.SearchPanelClosed)
                focus.clearFocus()
            },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .displayCutoutPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
        DetailSheet(
            overlay = detailOverlay,
            detailVm = detailViewModel,
            controller = controller,
            onDismiss = {
                detailOverlay = null
                mapViewModel.onEvent(MapEvent.SelectionCleared)
            },
            onNavigateToWriteReview = onNavigateToWriteReview,
            onFavoriteToggled = onFavoriteToggled
        )
    }
}

private fun OreumSummaryUiModel.asLatLng(): com.kakao.vectormap.LatLng =
    com.kakao.vectormap.LatLng.from(y, x)

private fun OreumSummaryUiModel.asGeoPoint(): GeoPoint = GeoPoint(y, x)

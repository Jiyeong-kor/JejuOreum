package com.jeong.feature.oreum.presentation.map

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.domain.entity.ResultSummary
import com.jeong.feature.oreum.presentation.detail.DetailViewModel
import com.jeong.oreum.presentation.map.DetailSheet
import com.jeong.oreum.presentation.map.MapController
import com.jeong.oreum.presentation.map.MapViewHost
import com.jeong.oreum.presentation.map.SearchPanel

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

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()
    val selectedOreum by mapViewModel.selectedOreum.collectAsStateWithLifecycle()
    val query by mapViewModel.searchQuery.collectAsStateWithLifecycle()

    val focus = LocalFocusManager.current

    var detailOverlay by remember { mutableStateOf<ResultSummary?>(null) }
    var controller by remember { mutableStateOf<MapController?>(null) }

    BackHandler(enabled = uiState is MapUiState.SearchResults || uiState is MapUiState.NoResults) {
        mapViewModel.onSearchQueryChanged("")
        mapViewModel.closeSearchPanel()
        focus.clearFocus()
    }

    LaunchedEffect(selectedOreum) { selectedOreum?.let { detailOverlay = it } }
    LaunchedEffect(detailOverlay) { detailOverlay?.let { detailViewModel.setOreumDetail(it) } }

    Box(modifier.fillMaxSize()) {
        MapViewHost(
            viewModel = mapViewModel,
            modifier = Modifier.fillMaxSize(),
            onMapReady = { controller = it },
            onMapTap = {
                detailOverlay = null
                mapViewModel.closeSearchPanel()
                mapViewModel.onSearchQueryChanged("")
                focus.clearFocus()
            }
        )
        SearchPanel(
            query = query,
            uiState = uiState,
            onQueryChange = { q -> mapViewModel.onSearchQueryChanged(q) },
            onResultClick = { item ->
                controller?.selectMarkerAt(item.asLatLng())
                controller?.moveCameraTo(item.asLatLng())
                detailOverlay = item
                mapViewModel.onSearchQueryChanged("")
                mapViewModel.closeSearchPanel()
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
                mapViewModel.clearSelection()
            },
            onNavigateToWriteReview = onNavigateToWriteReview,
            onFavoriteToggled = onFavoriteToggled
        )
    }
}

private fun ResultSummary.asLatLng(): com.kakao.vectormap.LatLng =
    com.kakao.vectormap.LatLng.from(y, x)

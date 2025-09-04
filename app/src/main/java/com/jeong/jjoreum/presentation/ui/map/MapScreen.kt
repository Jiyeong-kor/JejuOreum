package com.jeong.jjoreum.presentation.ui.map

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
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.viewmodel.DetailViewModel
import com.jeong.jjoreum.presentation.viewmodel.MapViewModel
import com.kakao.vectormap.LatLng

private tailrec fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun MapScreen(
    onNavigateToWriteReview: (Int, String) -> Unit,
    onFavoriteToggled: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    requireNotNull(activity)

    val viewModel: MapViewModel = hiltViewModel(activity)
    val detailVm: DetailViewModel = hiltViewModel(activity)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedOreum by viewModel.selectedOreum.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    val focus = LocalFocusManager.current

    var detailOverlay by remember { mutableStateOf<ResultSummary?>(null) }
    var controller by remember { mutableStateOf<MapController?>(null) }

    BackHandler(enabled = uiState is MapUiState.SearchResults || uiState is MapUiState.NoResults) {
        viewModel.onSearchQueryChanged("")
        viewModel.closeSearchPanel()
        focus.clearFocus()
    }
    LaunchedEffect(selectedOreum) { selectedOreum?.let { detailOverlay = it } }
    LaunchedEffect(detailOverlay) { detailOverlay?.let { detailVm.setOreumDetail(it) } }

    Box(Modifier.fillMaxSize()) {
        MapViewHost(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize(),
            onMapReady = { controller = it },
            onMapTap = {
                detailOverlay = null
                viewModel.closeSearchPanel()
                viewModel.onSearchQueryChanged("")
                focus.clearFocus()
            }
        )
        SearchPanel(
            query = query,
            uiState = uiState,
            onQueryChange = { q -> viewModel.onSearchQueryChanged(q) },
            onResultClick = { item ->
                val ll = LatLng.from(item.y, item.x)
                controller?.selectMarkerAt(ll)
                controller?.moveCameraTo(ll)
                detailOverlay = item
                viewModel.onSearchQueryChanged("")
                viewModel.closeSearchPanel()
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
            detailVm = detailVm,
            controller = controller,
            onDismiss = {
                detailOverlay = null
                viewModel.clearSelection()
            },
            onNavigateToWriteReview = onNavigateToWriteReview,
            onFavoriteToggled = onFavoriteToggled
        )
    }
}

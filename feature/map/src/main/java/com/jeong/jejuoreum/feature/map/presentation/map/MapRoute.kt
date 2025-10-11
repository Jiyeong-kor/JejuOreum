package com.jeong.jejuoreum.feature.map.presentation.map

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jejuoreum.core.common.UiText
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.feature.map.presentation.map.MapEffect.ShowMessage
import kotlinx.coroutines.flow.collectLatest

private tailrec fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun MapRoute(
    onNavigateToDetail: (OreumSummaryUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    requireNotNull(activity)

    val mapViewModel: MapViewModel = hiltViewModel(activity)
    val mapState by mapViewModel.uiState.collectAsStateWithLifecycle()
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

    LaunchedEffect(Unit) {
        mapViewModel.effect.collectLatest { effect ->
            when (effect) {
                is ShowMessage ->
                    Toast.makeText(
                        context,
                        effect.message.asString(context),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }
    }

    Box(modifier.fillMaxSize()) {
        MapViewHost(
            uiState = mapState,
            modifier = Modifier.fillMaxSize(),
            onEvent = mapViewModel::onEvent,
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
            controller = controller,
            onDismiss = {
                detailOverlay = null
                mapViewModel.onEvent(MapEvent.SelectionCleared)
            },
            onNavigateToDetail = { oreum ->
                onNavigateToDetail(oreum)
                detailOverlay = null
            }
        )
        if (mapState.isLoading) {
            LoadingIndicatorOverlay()
        }
    }
}

@Composable
private fun LoadingIndicatorOverlay() {
    Surface(
        modifier = Modifier.fillMaxSize(),
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

private fun UiText.asString(context: Context): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.StringResource -> context.getString(resId, *args)
}

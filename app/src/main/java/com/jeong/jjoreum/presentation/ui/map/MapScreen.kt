package com.jeong.jjoreum.presentation.ui.map

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.ui.detail.DetailScreen
import com.jeong.jjoreum.presentation.ui.detail.DetailViewModel
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory

private tailrec fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToWriteReview: (Int, String) -> Unit,
    onFavoriteToggled: (String) -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    requireNotNull(activity)

    val viewModel: MapViewModel = hiltViewModel(activity)
    val detailVm: DetailViewModel = hiltViewModel(activity)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val visiblePins by viewModel.visiblePins.collectAsStateWithLifecycle()
    val selectedOreum by viewModel.selectedOreum.collectAsStateWithLifecycle()
    val cameraState by viewModel.cameraState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val focus = LocalFocusManager.current

    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    var renderer by remember { mutableStateOf<MapRenderer?>(null) }
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }
    var isReady by remember { mutableStateOf(false) }

    var detailOverlay by remember { mutableStateOf<ResultSummary?>(null) }

    val mapView = remember(context) { MapView(context).apply { isFinishManually = true } }

    BackHandler(enabled = uiState is MapUiState.SearchResults || uiState is MapUiState.NoResults) {
        viewModel.onSearchQueryChanged("")
        viewModel.closeSearchPanel()
        focus.clearFocus()
    }

    LaunchedEffect(visiblePins, renderer, isReady) {
        if (isReady) renderer?.syncMarkers(visiblePins)
    }

    LaunchedEffect(selectedOreum) { selectedOreum?.let { detailOverlay = it } }
    LaunchedEffect(detailOverlay) { detailOverlay?.let { detailVm.setOreumDetail(it) } }

    Box(Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView.start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() {
                            renderer?.release()
                            renderer = null
                            kakaoMap?.apply {
                                setOnPoiClickListener(null)
                                setOnViewportClickListener(null)
                                setOnCameraMoveEndListener(null)
                            }
                            kakaoMap = null
                        }

                        override fun onMapError(error: Exception) {
                            Log.e("MapScreen", "Map error: ${error.message}")
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(map: KakaoMap) {
                            kakaoMap = map
                            renderer = MapRenderer(map)
                            isReady = true

                            val saved = cameraState
                            if (saved != null) {
                                map.moveCamera(
                                    CameraUpdateFactory.newCenterPosition(
                                        LatLng.from(
                                            saved.center.lat, saved.center.lon
                                        ),
                                        saved.zoomLevel
                                    )
                                )
                            } else {
                                map.moveCamera(
                                    CameraUpdateFactory.newCenterPosition(
                                        LatLng.from(33.3616, 126.5312),
                                        11
                                    )
                                )
                            }

                            val vp = map.viewport
                            val sw = map.fromScreenPoint(vp.left, vp.bottom)
                            val ne = map.fromScreenPoint(vp.right, vp.top)
                            if (sw != null && ne != null) {
                                viewModel.updateVisibleOreumWithin(asGeoBounds(sw, ne))
                            }

                            map.setOnPoiClickListener { _, latLng, _, _ ->
                                renderer?.selectMarkerAt(latLng)
                                renderer?.moveCameraTo(latLng)
                                viewModel.selectOreumAt(latLng.asGeoPoint())
                            }
                            map.setOnViewportClickListener { _, _, _ ->
                                renderer?.clearSelection()
                                viewModel.clearSelection()
                                viewModel.closeSearchPanel()
                                viewModel.onSearchQueryChanged("")
                                focus.clearFocus()
                            }
                            map.setOnCameraMoveEndListener { m, _, _ ->
                                val v = m.viewport
                                val sw2 = m.fromScreenPoint(v.left, v.bottom)
                                val ne2 = m.fromScreenPoint(v.right, v.top)
                                if (sw2 != null && ne2 != null) {
                                    viewModel.updateVisibleOreumWithin(
                                        asGeoBounds(sw2, ne2)
                                    )
                                }
                                val cx = (v.left + v.right) / 2
                                val cy = (v.top + v.bottom) / 2
                                m.fromScreenPoint(cx, cy)?.let { c ->
                                    viewModel
                                        .saveCamera(c.asGeoPoint(), m.zoomLevel)
                                }
                            }
                        }
                    }
                )
                mapView
            }
        )

        SearchPanel(
            query = query,
            uiState = uiState,
            onQueryChange = { q -> viewModel.onSearchQueryChanged(q) },
            onResultClick = { item ->
                val ll = LatLng.from(item.y, item.x)
                renderer?.selectMarkerAt(ll)
                renderer?.moveCameraTo(ll)
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

        val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        if (detailOverlay != null) {
            DisposableEffect(Unit) {
                mapView.pause()
                onDispose { mapView.resume() }
            }
            ModalBottomSheet(
                onDismissRequest = {
                    detailOverlay = null
                    viewModel.clearSelection()
                },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    DetailScreen(
                        viewModel = detailVm,
                        onNavigateToWriteReview = onNavigateToWriteReview,
                        showToast = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onFavoriteToggled = onFavoriteToggled
                    )
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, e ->
            when (e) {
                Lifecycle.Event.ON_RESUME -> mapView.resume()
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                Lifecycle.Event.ON_STOP -> {
                    detailOverlay = null
                    viewModel.clearSelection()
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            renderer?.release()
            renderer = null
            kakaoMap?.apply {
                setOnPoiClickListener(null)
                setOnViewportClickListener(null)
                setOnCameraMoveEndListener(null)
            }
            kakaoMap = null
            mapView.finish()
        }
    }
}

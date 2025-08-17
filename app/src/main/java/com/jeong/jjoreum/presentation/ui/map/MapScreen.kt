package com.jeong.jjoreum.presentation.ui.map

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.ui.detail.DetailScreen
import com.jeong.jjoreum.presentation.ui.detail.DetailViewModel
import com.jeong.jjoreum.domain.geo.GeoPoint
import com.jeong.jjoreum.domain.geo.GeoBounds
import com.jeong.jjoreum.domain.geo.quantized
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.CompetitionType
import com.kakao.vectormap.label.CompetitionUnit
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelLayerOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle
import com.kakao.vectormap.label.OrderingType

data class MapPinUi(
    val title: String,
    val lat: Double,
    val lon: Double
)

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

    var query by remember { mutableStateOf("") }

    var renderer by remember { mutableStateOf<MapRenderer?>(null) }
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }
    var isReady by remember { mutableStateOf(false) }

    var detailOverlay by remember { mutableStateOf<ResultSummary?>(null) }

    val mapView = remember(context) { MapView(context).apply { isFinishManually = true } }

    BackHandler(enabled = uiState is MapUiState.SearchResults || uiState is MapUiState.NoResults) {
        viewModel.closeSearchPanel()
        query = ""
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
                                query = ""
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

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .displayCutoutPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { q ->
                    query = q
                    viewModel.onSearchQueryChanged(q)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("오름의 이름이나 주소로 검색해 주세요") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.onSearchQueryChanged(query)
                        focus.clearFocus()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    errorContainerColor = Color.White
                )
            )

            when (val state = uiState) {
                is MapUiState.SearchResults -> {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                        ) {
                            items(state.list, key = { "${it.x},${it.y}" }) { item ->
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .clickable {
                                            val ll = LatLng.from(item.y, item.x)
                                            renderer?.selectMarkerAt(ll)
                                            renderer?.moveCameraTo(ll)
                                            detailOverlay = item
                                            query = ""
                                            viewModel.closeSearchPanel()
                                            focus.clearFocus()
                                        }
                                        .padding(12.dp)
                                ) {
                                    AsyncImage(
                                        model = item.imgPath,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp),
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        item.oreumKname,
                                        fontSize = 16.sp,
                                        color = Color.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        item.oreumAddr,
                                        fontSize = 14.sp,
                                        color = Color.DarkGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                HorizontalDivider(
                                    thickness = DividerDefaults.Thickness,
                                    color = DividerDefaults.color
                                )
                            }
                        }
                    }
                }

                is MapUiState.NoResults -> {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) { Text("검색 결과가 없어요.") }
                }

                is MapUiState.Hidden, is MapUiState.Idle -> Unit
            }
        }

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

private class MapRenderer(private val map: KakaoMap) {

    private val labelLayer: LabelLayer = map.labelManager?.addLayer(
        LabelLayerOptions.from("oreumLayer")
            .setOrderingType(OrderingType.Rank)
            .setCompetitionUnit(CompetitionUnit.IconAndText)
            .setCompetitionType(CompetitionType.All)
    )?.apply {
        isVisible = true
        isClickable = true
    } ?: error("LabelLayer 생성 실패")

    private val markersByPoint = mutableMapOf<GeoPoint, Label>()
    private var selectedLabel: Label? = null

    private val unselectedStyle: LabelStyles by lazy(LazyThreadSafetyMode.NONE) {
        LabelStyles.from(
            LabelStyle.from(R.drawable.oreum_unselected).apply {
                setTextStyles(
                    LabelTextStyle
                        .from(24, 0xFF000000.toInt(), 0, 0)
                )
            }
        )
    }
    private val selectedStyle: LabelStyles by lazy(LazyThreadSafetyMode.NONE) {
        LabelStyles.from(
            LabelStyle.from(R.drawable.oreum_selected).apply {
                setTextStyles(
                    LabelTextStyle
                        .from(32, 0xFF000000.toInt(), 0, 0)
                )
            }
        )
    }

    private fun MapPinUi.quantKey(): GeoPoint = GeoPoint(lat, lon).quantized()
    private fun LatLng.quantKey(): GeoPoint = GeoPoint(latitude, longitude).quantized()

    fun syncMarkers(pins: List<MapPinUi>) {
        val newKeyByPin = pins.associateBy { it.quantKey() }
        val oldKeys = markersByPoint.keys
        val newKeys = newKeyByPin.keys

        if (oldKeys.size == newKeys.size && oldKeys.containsAll(newKeys)) return

        val wasVisible = labelLayer.isVisible
        if (wasVisible) labelLayer.isVisible = false
        try {
            (oldKeys - newKeys).forEach { key ->
                markersByPoint.remove(key)?.let { labelLayer.remove(it) }
            }
            (newKeys - oldKeys).forEach { key ->
                val p = newKeyByPin.getValue(key)
                val label = labelLayer.addLabel(
                    com.kakao.vectormap.label.LabelOptions
                        .from(LatLng.from(p.lat, p.lon))
                        .setTexts(LabelTextBuilder().setTexts(p.title))
                        .setStyles(unselectedStyle)
                        .setTag("oreum:${p.title}")
                )
                markersByPoint[key] = label
            }
        } finally {
            labelLayer.isVisible = wasVisible
        }
    }

    fun selectMarkerAt(latLng: LatLng) {
        selectedLabel?.changeStyles(unselectedStyle)
        val label = markersByPoint[latLng.quantKey()]
        label?.changeStyles(selectedStyle)
        selectedLabel = label
    }

    fun clearSelection() {
        selectedLabel?.changeStyles(unselectedStyle)
        selectedLabel = null
    }

    fun moveCameraTo(latLng: LatLng) {
        map.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
    }

    fun release() {
        runCatching { labelLayer.removeAll() }
            .onFailure { Log.w("MapRenderer", "removeAll 실패", it) }
        markersByPoint.clear()
        selectedLabel = null
    }
}

private fun LatLng.asGeoPoint(): GeoPoint =
    GeoPoint(getLatitude(), getLongitude())

private fun asGeoBounds(sw: LatLng, ne: LatLng): GeoBounds =
    GeoBounds(
        sw = GeoPoint(sw.getLatitude(), sw.getLongitude()),
        ne = GeoPoint(ne.getLatitude(), ne.getLongitude())
    )
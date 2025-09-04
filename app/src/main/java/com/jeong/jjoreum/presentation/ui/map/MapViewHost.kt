package com.jeong.jjoreum.presentation.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jjoreum.presentation.viewmodel.MapViewModel
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import timber.log.Timber

class MapController(
    val selectMarkerAt: (LatLng) -> Unit,
    val moveCameraTo: (LatLng) -> Unit,
    val pause: () -> Unit,
    val resume: () -> Unit
)

@Composable
fun MapViewHost(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier,
    onMapReady: (MapController) -> Unit = {},
    onMapTap: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val visiblePins by viewModel.visiblePins.collectAsStateWithLifecycle()
    val cameraState by viewModel.cameraState.collectAsStateWithLifecycle()

    var renderer by remember { mutableStateOf<MapRenderer?>(null) }
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }
    var isReady by remember { mutableStateOf(false) }

    val mapView = remember(context) { MapView(context).apply { isFinishManually = true } }

    LaunchedEffect(visiblePins, renderer, isReady) {
        if (isReady) renderer?.syncMarkers(visiblePins)
    }
    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.resume()
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                Lifecycle.Event.ON_STOP -> viewModel.clearSelection()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            renderer?.release()
            renderer = null
            kakaoMap?.let { map ->
                runCatching {
                    map.setOnPoiClickListener(null)
                    map.setOnViewportClickListener(null)
                    map.setOnCameraMoveEndListener(null)
                }
            }
            kakaoMap = null
            mapView.finish()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.start(
                object : MapLifeCycleCallback() {
                    override fun onMapDestroy() {
                        renderer?.release()
                        renderer = null
                        kakaoMap?.let { map ->
                            runCatching {
                                map.setOnPoiClickListener(null)
                                map.setOnViewportClickListener(null)
                                map.setOnCameraMoveEndListener(null)
                            }
                        }
                        kakaoMap = null
                    }

                    override fun onMapError(error: Exception) {
                        Timber.e(error, "Map error: %s", error.message)
                    }
                },
                object : KakaoMapReadyCallback() {
                    override fun onMapReady(map: KakaoMap) {
                        kakaoMap = map
                        renderer = MapRenderer(map)
                        isReady = true
                        onMapReady(
                            MapController(
                                selectMarkerAt = { ll -> renderer?.selectMarkerAt(ll) },
                                moveCameraTo = { ll -> renderer?.moveCameraTo(ll) },
                                pause = { mapView.pause() },
                                resume = { mapView.resume() }
                            )
                        )
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
                            onMapTap()
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
                                viewModel.saveCamera(c.asGeoPoint(), m.zoomLevel)
                            }
                        }
                    }
                }
            )
            mapView
        }
    )
}

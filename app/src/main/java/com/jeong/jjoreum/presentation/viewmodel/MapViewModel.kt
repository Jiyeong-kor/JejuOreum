package com.jeong.jjoreum.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.domain.geo.GeoBounds
import com.jeong.jjoreum.domain.geo.GeoPoint
import com.jeong.jjoreum.domain.geo.quantized
import com.jeong.jjoreum.presentation.ui.map.MapPinUi
import com.jeong.jjoreum.presentation.ui.map.MapUiState
import com.jeong.jjoreum.repository.OreumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

data class CameraSnapshot(val center: GeoPoint, val zoomLevel: Int)

@HiltViewModel
class MapViewModel @Inject constructor(
    repository: OreumRepository,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    private companion object {
        const val KEY_CAM_LAT = "cam_lat"
        const val KEY_CAM_LON = "cam_lon"
        const val KEY_CAM_ZOOM = "cam_zoom"
    }

    private val oreumList: StateFlow<List<ResultSummary>> = repository.oreumListFlow

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Idle)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _visiblePins = MutableStateFlow<List<MapPinUi>>(emptyList())
    val visiblePins: StateFlow<List<MapPinUi>> = _visiblePins.asStateFlow()

    private val _selectedOreum = MutableStateFlow<ResultSummary?>(null)
    val selectedOreum: StateFlow<ResultSummary?> = _selectedOreum.asStateFlow()

    private val _cameraState = MutableStateFlow(restoreCameraFromSavedState())
    val cameraState: StateFlow<CameraSnapshot?> = _cameraState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            val q = query.trim()
            if (q.isBlank()) {
                _uiState.value = MapUiState.Hidden
                return@launch
            }
            val result = oreumList.value.filter { item ->
                item.oreumKname.contains(q) || item.oreumAddr.contains(q)
            }
            _uiState.value =
                if (result.isEmpty()) MapUiState.NoResults
                else MapUiState.SearchResults(result)
        }
    }

    private data class ViewportSnapshot(val bounds: GeoBounds, val zoom: Int)

    private var lastViewport: ViewportSnapshot? = null

    fun updateVisibleOreumWithin(bounds: GeoBounds, zoomLevel: Int) {
        val snapshot = ViewportSnapshot(bounds, zoomLevel)
        if (snapshot == lastViewport) return
        lastViewport = snapshot
        viewModelScope.launch(Dispatchers.Default) {
            val full = oreumList.value
            val minLat = min(bounds.sw.lat, bounds.ne.lat)
            val maxLat = max(bounds.sw.lat, bounds.ne.lat)
            val minLon = min(bounds.sw.lon, bounds.ne.lon)
            val maxLon = max(bounds.sw.lon, bounds.ne.lon)

            val precision = zoomLevel.toClusterPrecision()
            val clusters =
                LinkedHashMap<GeoPoint, MutableList<ResultSummary>>()
            for (o in full) {
            if (o.y in minLat..maxLat && o.x in minLon..maxLon) {
                val key = GeoPoint(o.y, o.x).quantized(precision)
                clusters.getOrPut(key) { mutableListOf() }.add(o)
            }
        }
            val pins = buildList {
                for ((key, group) in clusters) {
                    if (group.size == 1) {
                        val oreum = group.first()
                        add(
                            MapPinUi.Single(
                                lat = oreum.y,
                                lon = oreum.x,
                                title = oreum.oreumKname,
                                oreumId = oreum.idx,
                            )
                        )
                    } else {
                        val avgLat = group.sumOf { it.y } / group.size
                        val avgLon = group.sumOf { it.x } / group.size
                        add(
                            MapPinUi.Cluster(
                                lat = avgLat.takeIf { it.isFinite() } ?: key.lat,
                                lon = avgLon.takeIf { it.isFinite() } ?: key.lon,
                                count = group.size,
                            )
                        )
                    }
                }
            }.sortedWith(
                compareBy<MapPinUi> { it.lat }
                    .thenBy { it.lon }
                    .thenBy {
                        when (it) {
                            is MapPinUi.Cluster -> it.count.toDouble()
                            is MapPinUi.Single -> it.oreumId.toDouble()
                        }
                    }
            )
            if (pins != _visiblePins.value) {
                withContext(Dispatchers.Main) { _visiblePins.value = pins }
            }
        }
    }

    private fun ResultSummary.quantKey(): GeoPoint = GeoPoint(y, x).quantized()

    fun selectOreumAt(point: GeoPoint): ResultSummary? {
        val key = point.quantized()
        _selectedOreum.value = oreumList.value.find { it.quantKey() == key }
        return _selectedOreum.value
    }

    fun clearSelection() {
        _selectedOreum.value = null
    }

    fun closeSearchPanel() {
        _uiState.value = MapUiState.Hidden
    }

    fun saveCamera(center: GeoPoint, zoomLevel: Int) {
        _cameraState.value = CameraSnapshot(center, zoomLevel)
        savedStateHandle[KEY_CAM_LAT] = center.lat
        savedStateHandle[KEY_CAM_LON] = center.lon
        savedStateHandle[KEY_CAM_ZOOM] = zoomLevel
    }

    private fun restoreCameraFromSavedState(): CameraSnapshot? {
        val lat = savedStateHandle.get<Double>(KEY_CAM_LAT)
        val lon = savedStateHandle.get<Double>(KEY_CAM_LON)
        val zoom = savedStateHandle.get<Int>(KEY_CAM_ZOOM)
        return if (lat != null && lon != null && zoom != null) {
            CameraSnapshot(GeoPoint(lat, lon), zoom)
        } else null
    }

    private fun Int.toClusterPrecision(): Int = when {
        this >= 13 -> 5
        this >= 11 -> 4
        this >= 9 -> 3
        this >= 7 -> 2
        else -> 1
    }
}

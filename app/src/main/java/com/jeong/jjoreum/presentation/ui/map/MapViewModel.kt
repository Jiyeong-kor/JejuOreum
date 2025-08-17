package com.jeong.jjoreum.presentation.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.domain.geo.GeoBounds
import com.jeong.jjoreum.domain.geo.GeoPoint
import com.jeong.jjoreum.domain.geo.quantized
import com.jeong.jjoreum.repository.OreumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            val q = query.trim()
            val result = oreumList.value.filter { item ->
                q.isBlank()
                        || item.oreumKname.contains(q)
                        || item.oreumAddr.contains(q)
            }
            _uiState.value =
                if (result.isEmpty() && q.isNotBlank()) MapUiState.NoResults
                else MapUiState.SearchResults(result)
        }
    }

    fun updateVisibleOreumWithin(bounds: GeoBounds) {
        viewModelScope.launch {
            val full = oreumList.value
            val minLat = min(bounds.sw.lat, bounds.ne.lat)
            val maxLat = max(bounds.sw.lat, bounds.ne.lat)
            val minLon = min(bounds.sw.lon, bounds.ne.lon)
            val maxLon = max(bounds.sw.lon, bounds.ne.lon)

            val newPins = full.filter { o ->
                o.y in minLat..maxLat && o.x in minLon..maxLon
            }
                .map { o -> MapPinUi(title = o.oreumKname, lat = o.y, lon = o.x) }

            if (newPins == _visiblePins.value) return@launch
            _visiblePins.value = newPins
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
}
package com.jeong.feature.oreum.presentation.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.domain.entity.GeoBounds
import com.jeong.domain.entity.GeoPoint
import com.jeong.domain.entity.quantized
import com.jeong.domain.usecase.oreum.FilterOreumsWithinBoundsUseCase
import com.jeong.domain.usecase.oreum.FindOreumByLocationUseCase
import com.jeong.domain.usecase.oreum.ObserveOreumSummariesUseCase
import com.jeong.domain.usecase.oreum.SearchOreumsUseCase
import com.jeong.domain.entity.ResultSummary
import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel
import com.jeong.feature.oreum.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val KEY_CAM_LAT = "cam_lat"
private const val KEY_CAM_LON = "cam_lon"
private const val KEY_CAM_ZOOM = "cam_zoom"

data class CameraSnapshot(val center: GeoPoint, val zoomLevel: Int)

@HiltViewModel
class MapViewModel @Inject constructor(
    observeOreumSummariesUseCase: ObserveOreumSummariesUseCase,
    private val filterOreumsWithinBoundsUseCase: FilterOreumsWithinBoundsUseCase,
    private val searchOreumsUseCase: SearchOreumsUseCase,
    private val findOreumByLocationUseCase: FindOreumByLocationUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val oreumList: StateFlow<List<ResultSummary>> = observeOreumSummariesUseCase()

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Hidden)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _visiblePins = MutableStateFlow<List<MapPinUi>>(emptyList())
    val visiblePins: StateFlow<List<MapPinUi>> = _visiblePins.asStateFlow()

    private val _selectedOreum = MutableStateFlow<OreumSummaryUiModel?>(null)
    val selectedOreum: StateFlow<OreumSummaryUiModel?> = _selectedOreum.asStateFlow()

    private val _cameraState = MutableStateFlow(restoreCameraFromSavedState())
    val cameraState: StateFlow<CameraSnapshot?> = _cameraState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val pinCache = mutableMapOf<GeoPoint, MapPinUi>()
    private var lastBounds: GeoBounds? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch(Dispatchers.Default) {
            val results = searchOreumsUseCase(oreumList.value, query)
            withContext(Dispatchers.Main) {
                _uiState.value = when {
                    query.isBlank() -> MapUiState.Hidden
                    results.isEmpty() -> MapUiState.NoResults
                    else -> MapUiState.SearchResults(results.map { it.toUiModel() })
                }
            }
        }
    }

    fun updateVisibleOreumWithin(bounds: GeoBounds) {
        if (bounds == lastBounds) return
        lastBounds = bounds
        viewModelScope.launch(Dispatchers.Default) {
            val visible = filterOreumsWithinBoundsUseCase(oreumList.value, bounds)
            val pins = visible.map { summary ->
                val point = GeoPoint(summary.y, summary.x).quantized()
                pinCache.getOrPut(point) {
                    MapPinUi(summary.oreumKname, summary.y, summary.x)
                }
            }
            if (pins != _visiblePins.value) {
                withContext(Dispatchers.Main) { _visiblePins.value = pins }
            }
        }
    }

    fun selectOreumAt(point: GeoPoint): OreumSummaryUiModel? {
        val oreum = findOreumByLocationUseCase(oreumList.value, point)
        val uiModel = oreum?.toUiModel()
        _selectedOreum.value = uiModel
        return uiModel
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

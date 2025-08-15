package com.jeong.jjoreum.presentation.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.repository.OreumRepository
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class MapViewModel @Inject constructor(
    repository: OreumRepository
) : ViewModel() {

    private val _oreumList = repository.oreumListFlow

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Idle)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _visibleOreumList = MutableStateFlow<List<ResultSummary>>(emptyList())
    val visibleOreumList: StateFlow<List<ResultSummary>> = _visibleOreumList.asStateFlow()

    private val _selectedOreum = MutableStateFlow<ResultSummary?>(null)

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            val result = _oreumList.value.filter {
                query.isBlank()
                        || it.oreumKname.contains(query, true)
                        || it.oreumAddr.contains(
                    query,
                    true
                )
            }
            _uiState.value = if (result.isEmpty() && query.isNotBlank()) MapUiState.NoResults
            else MapUiState.SearchResults(result)
        }
    }

    fun loadOreumForVisibleArea(bounds: LatLngBounds) {
        viewModelScope.launch {
            val fullList = _oreumList.value
            val filteredList = fullList.filter { oreum ->
                val point = LatLng.from(oreum.y, oreum.x)
                point.latitude in bounds.southwest.latitude..bounds.northeast.latitude &&
                        point.longitude in
                        bounds.southwest.longitude..bounds.northeast.longitude
            }
            _visibleOreumList.value = filteredList
            Log.d("MapViewModel", "Visible Oreum count: ${filteredList.size}")
        }
    }

    fun selectOreum(latLng: LatLng) {
        _selectedOreum.value = _oreumList.value.find {
            isSameOreumLocation(it, latLng)
        } ?: run {
            Log.w("MapViewModel", "해당 위치에 오름 정보가 없습니다: $latLng")
            null
        }
    }

    fun onPoiClicked(latLng: LatLng): ResultSummary? {
        selectOreum(latLng)
        return _selectedOreum.value
    }

    fun onMapTouched() {
        _selectedOreum.value = null
    }

    fun hideSearch() {
        _uiState.value = MapUiState.Hidden
    }

    private fun isSameOreumLocation(oreum: ResultSummary, latLng: LatLng): Boolean =
        abs(oreum.y - latLng.latitude) < 0.0005 &&
                abs(oreum.x - latLng.longitude) < 0.0005
}
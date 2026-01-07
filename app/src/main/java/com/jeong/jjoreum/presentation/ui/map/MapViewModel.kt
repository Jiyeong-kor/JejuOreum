package com.jeong.jjoreum.presentation.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.repository.OreumRepository
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(FlowPreview::class)
class MapViewModel(
    private val repository: OreumRepository
) : ViewModel() {

    val oreumList: StateFlow<List<ResultSummary>> = repository.oreumListFlow
    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Idle)
    val uiState: StateFlow<MapUiState> = _uiState

    private val _selectedOreum = MutableStateFlow<ResultSummary?>(null)
    private val searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            repository.loadOreumListIfNeeded()
        }
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _uiState.value = MapUiState.Idle
                        return@collectLatest
                    }
                    val result = oreumList.value.filter {
                        it.oreumKname.contains(query, true) ||
                                it.oreumAddr.contains(
                                    query,
                                    true
                                )
                    }
                    _uiState.value = if (result.isEmpty() && query.isNotBlank()) {
                        MapUiState.NoResults
                    } else {
                        MapUiState.SearchResults(result)
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun selectOreum(latLng: LatLng) {
        _selectedOreum.value = oreumList.value.find {
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

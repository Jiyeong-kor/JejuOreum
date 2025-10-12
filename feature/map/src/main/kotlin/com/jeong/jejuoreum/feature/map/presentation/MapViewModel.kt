package com.jeong.jejuoreum.feature.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.common.result.JejuOreumResult
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveOreumsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.RefreshOreumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@HiltViewModel
class MapViewModel @Inject constructor(
    private val observeOreums: ObserveOreumsUseCase,
    private val refreshOreums: RefreshOreumsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        observeOreumList()
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = refreshOreums()) {
                is JejuOreumResult.Error -> _uiState.update { state ->
                    state.copy(isLoading = false, errorMessage = result.throwable.message)
                }
                is JejuOreumResult.Success -> _uiState.update { state ->
                    state.copy(isLoading = false)
                }
            }
        }
    }

    private fun observeOreumList() {
        viewModelScope.launch {
            observeOreums().collect { oreums ->
                _uiState.update { state ->
                    state.copy(oreums = oreums)
                }
            }
        }
    }
}

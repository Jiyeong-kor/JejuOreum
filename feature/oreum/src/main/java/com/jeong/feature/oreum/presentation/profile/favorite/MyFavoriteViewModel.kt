package com.jeong.feature.oreum.presentation.profile.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.domain.usecase.ToggleFavoriteUseCase
import com.jeong.feature.oreum.domain.usecase.LoadOreumSummariesUseCase
import com.jeong.feature.oreum.domain.usecase.ObserveFavoriteOreumsUseCase
import com.jeong.feature.oreum.domain.usecase.RefreshOreumSummariesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFavoriteViewModel @Inject constructor(
    private val loadOreumSummariesUseCase: LoadOreumSummariesUseCase,
    private val observeFavoriteOreumsUseCase: ObserveFavoriteOreumsUseCase,
    private val refreshOreumSummariesUseCase: RefreshOreumSummariesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyFavoriteUiState())
    val uiState: StateFlow<MyFavoriteUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val loadResult = loadOreumSummariesUseCase()
            if (loadResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = loadResult.exceptionOrNull()?.message
                    )
                }
                return@launch
            }

            observeFavoriteOreumsUseCase().collect { favorites ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        favorites = favorites,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun toggleFavorite(oreumIdx: String, newStatus: Boolean) {
        viewModelScope.launch {
            runCatching { toggleFavoriteUseCase(oreumIdx, newStatus) }
                .onSuccess { refreshOreumSummariesUseCase() }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

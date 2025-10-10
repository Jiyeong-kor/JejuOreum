package com.jeong.jejuoreum.feature.profile.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.domain.oreum.usecase.LoadOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveFavoriteOreumsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.RefreshOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ToggleFavoriteUseCase
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

            observeFavoriteOreumsUseCase().collect { resource ->
                when (resource) {
                    Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }

                    is Resource.Success -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            favorites = resource.data.map { oreum -> oreum.toProfileUiModel() },
                            errorMessage = null
                        )
                    }

                    is Resource.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = resource.throwable?.message
                                ?: "즐겨찾는 오름을 불러오지 못했어요."
                        )
                    }
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

private fun com.jeong.jejuoreum.domain.oreum.entity.ResultSummary.toProfileUiModel(): OreumSummaryUiModel =
    OreumSummaryUiModel(
        idx = idx,
        oreumEname = oreumEname,
        oreumKname = oreumKname,
        oreumAddr = oreumAddr,
        oreumAltitu = oreumAltitu,
        x = x,
        y = y,
        explain = explain,
        imgPath = imgPath,
        totalFavorites = totalFavorites,
        totalStamps = totalStamps,
        userLiked = userLiked,
        userStamped = userStamped,
    )

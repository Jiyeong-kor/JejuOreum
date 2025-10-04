package com.jeong.feature.oreum.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.usecase.ToggleFavoriteUseCase
import com.jeong.domain.usecase.oreum.GetUserStampStatusesUseCase
import com.jeong.domain.usecase.oreum.LoadOreumSummariesUseCase
import com.jeong.domain.usecase.oreum.ObserveOreumSummariesUseCase
import com.jeong.domain.usecase.oreum.TryStampUseCase
import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel
import com.jeong.feature.oreum.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val loadOreumSummariesUseCase: LoadOreumSummariesUseCase,
    observeOreumSummariesUseCase: ObserveOreumSummariesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val tryStampUseCase: TryStampUseCase,
    private val getUserStampStatusesUseCase: GetUserStampStatusesUseCase
) : ViewModel() {
    private val oreumListFlow: StateFlow<List<ResultSummary>> = observeOreumSummariesUseCase()

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ListEvent>()
    val events: SharedFlow<ListEvent> = _events.asSharedFlow()

    init {
        observeOreums()
        refreshOreumsIfNeeded()
    }

    fun onFavoriteClick(summary: OreumSummaryUiModel) {
        viewModelScope.launch {
            val newTotal = toggleFavoriteUseCase(
                oreumIdx = summary.idx.toString(),
                newIsFavorite = !summary.userLiked,
            )
            _uiState.update { state ->
                state.copy(
                    oreums = state.oreums.map { item ->
                        if (item.idx == summary.idx) {
                            item.copy(
                                userLiked = !summary.userLiked,
                                totalFavorites = newTotal,
                            )
                        } else item
                    },
                )
            }
        }
    }

    fun onStampClick(summary: OreumSummaryUiModel) {
        viewModelScope.launch {
            val result = tryStampUseCase(
                oreumIdx = summary.idx.toString(),
                oreumName = summary.oreumKname,
                oreumLat = summary.y,
                oreumLng = summary.x,
            )
            if (result.isSuccess) {
                updateStampStatuses()
                _events.emit(ListEvent.StampSuccess)
            } else {
                _events.emit(
                    ListEvent.StampFailure(
                        result.exceptionOrNull()?.message,
                    ),
                )
            }
        }
    }

    private fun observeOreums() {
        viewModelScope.launch {
            oreumListFlow.collectLatest { summaries ->
                val enriched = applyStampStatuses(summaries)
                _uiState.update { state ->
                    state.copy(
                        oreums = enriched,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun refreshOreumsIfNeeded() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = loadOreumSummariesUseCase()
            if (result.isFailure) {
                _uiState.update { state ->
                    state.copy(isLoading = false)
                }
                _events.emit(
                    ListEvent.LoadFailed(result.exceptionOrNull()?.message),
                )
            }
        }
    }

    private suspend fun applyStampStatuses(oreums: List<ResultSummary>): List<OreumSummaryUiModel> {
        val statuses = fetchStampStatuses()
        return oreums.map { summary ->
            val key = summary.idx.toString()
            summary.copy(userStamped = statuses[key] ?: summary.userStamped).toUiModel()
        }
    }

    private suspend fun updateStampStatuses() {
        val statuses = fetchStampStatuses()
        _uiState.update { state ->
            state.copy(
                oreums = state.oreums.map { summary ->
                    val key = summary.idx.toString()
                    summary.copy(userStamped = statuses[key] ?: summary.userStamped)
                },
            )
        }
    }

    private suspend fun fetchStampStatuses(): Map<String, Boolean> =
        runCatching { getUserStampStatusesUseCase() }.getOrDefault(emptyMap())
}

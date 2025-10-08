package com.jeong.feature.oreum.presentation.list

import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.ui.viewmodel.BaseViewModel
import com.jeong.core.utils.coroutines.CoroutineDispatcherProvider
import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.usecase.ToggleFavoriteUseCase
import com.jeong.domain.usecase.oreum.GetUserStampStatusesUseCase
import com.jeong.domain.usecase.oreum.LoadOreumSummariesUseCase
import com.jeong.domain.usecase.oreum.ObserveOreumSummariesUseCase
import com.jeong.domain.usecase.oreum.TryStampUseCase
import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel
import com.jeong.feature.oreum.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ListViewModel @Inject constructor(
    private val loadOreumSummariesUseCase: LoadOreumSummariesUseCase,
    observeOreumSummariesUseCase: ObserveOreumSummariesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val tryStampUseCase: TryStampUseCase,
    private val getUserStampStatusesUseCase: GetUserStampStatusesUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : BaseViewModel<ListUiEvent, ListUiEffect, ListUiState>(ListUiState(isLoading = true)) {

    private val oreumListFlow: StateFlow<List<ResultSummary>> = observeOreumSummariesUseCase()
    private var observeJob: Job? = null
    private var cachedStampStatuses: Map<String, Boolean> = emptyMap()

    init {
        onEvent(ListUiEvent.ScreenInitialized)
    }

    override fun onCleared() {
        observeJob?.cancel()
        observeJob = null
        super.onCleared()
    }

    override fun handleEvent(event: ListUiEvent) {
        when (event) {
            ListUiEvent.ScreenInitialized -> initialize()
            is ListUiEvent.FavoriteToggled -> handleFavoriteToggle(event.summary)
            is ListUiEvent.StampRequested -> handleStampRequest(event.summary)
            ListUiEvent.RefreshRequested -> refreshOreums(force = true)
        }
    }

    private fun initialize() {
        startObservation()
        refreshOreums(force = false)
    }

    private fun startObservation() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch(dispatcherProvider.io) {
            oreumListFlow.collectLatest { summaries ->
                val statuses = refreshStampStatuses()
                val uiModels = summaries.mapToUi(statuses)
                withContext(dispatcherProvider.main) {
                    setState { copy(oreums = uiModels, isLoading = false) }
                }
            }
        }
    }

    private fun refreshOreums(force: Boolean) {
        viewModelScope.launch(dispatcherProvider.io) {
            if (force) {
                withContext(dispatcherProvider.main) { setState { copy(isLoading = true) } }
            }
            val result = loadOreumSummariesUseCase()
            if (result.isFailure) {
                sendEffect { ListUiEffect.LoadFailed(result.exceptionOrNull()?.message) }
                withContext(dispatcherProvider.main) { setState { copy(isLoading = false) } }
            }
        }
    }

    private fun handleFavoriteToggle(summary: OreumSummaryUiModel) {
        viewModelScope.launch(dispatcherProvider.io) {
            runCatching {
                toggleFavoriteUseCase(
                    oreumIdx = summary.idx.toString(),
                    newIsFavorite = !summary.userLiked,
                )
            }.onSuccess { newTotal ->
                withContext(dispatcherProvider.main) {
                    setState {
                        copy(
                            oreums = oreums.map { item ->
                                if (item.idx == summary.idx) {
                                    item.copy(
                                        userLiked = !summary.userLiked,
                                        totalFavorites = newTotal,
                                    )
                                } else {
                                    item
                                }
                            }
                        )
                    }
                }
            }.onFailure { throwable ->
                sendEffect { ListUiEffect.FavoriteUpdateFailed(throwable.message) }
            }
        }
    }

    private fun handleStampRequest(summary: OreumSummaryUiModel) {
        viewModelScope.launch(dispatcherProvider.io) {
            val result = tryStampUseCase(
                oreumIdx = summary.idx.toString(),
                oreumName = summary.oreumKname,
                oreumLat = summary.y,
                oreumLng = summary.x,
            )
            if (result.isSuccess) {
                refreshStampStatuses(force = true)
                withContext(dispatcherProvider.main) {
                    setState {
                        copy(
                            oreums = oreums.map { item ->
                                if (item.idx == summary.idx) {
                                    item.copy(userStamped = true)
                                } else {
                                    item
                                }
                            }
                        )
                    }
                }
                sendEffect { ListUiEffect.StampSuccess }
            } else {
                sendEffect { ListUiEffect.StampFailure(result.exceptionOrNull()?.message) }
            }
        }
    }

    private suspend fun refreshStampStatuses(force: Boolean = false): Map<String, Boolean> {
        if (!force && cachedStampStatuses.isNotEmpty()) {
            return cachedStampStatuses
        }
        cachedStampStatuses = runCatching {
            withContext(dispatcherProvider.io) { getUserStampStatusesUseCase() }
        }.getOrDefault(emptyMap())
        return cachedStampStatuses
    }

    private fun List<ResultSummary>.mapToUi(statuses: Map<String, Boolean>):
            List<OreumSummaryUiModel> =
        map { summary ->
            val key = summary.idx.toString()
            val stamped = statuses[key] ?: summary.userStamped
            summary.copy(userStamped = stamped).toUiModel()
        }
}

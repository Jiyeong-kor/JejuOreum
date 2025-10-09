package com.jeong.jejuoreum.feature.oreum.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.ui.viewmodel.BaseViewModel
import com.jeong.jejuoreum.feature.oreum.domain.OreumOverviewInteractor
import com.jeong.jejuoreum.feature.oreum.domain.model.OreumOverview
import com.jeong.jejuoreum.feature.oreum.presentation.mapper.OreumUiMapper
import com.jeong.jejuoreum.feature.oreum.presentation.model.OreumUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class OreumViewModel @Inject constructor(
    private val interactor: OreumOverviewInteractor,
    private val uiMapper: OreumUiMapper
) : BaseViewModel<OreumEvent, OreumEffect, OreumUiState>(OreumUiState()) {

    private var observeJob: Job? = null

    init {
        onEvent(OreumEvent.ScreenInitialized)
    }

    override fun handleEvent(event: OreumEvent) {
        when (event) {
            OreumEvent.ScreenInitialized -> startObservation()
            is OreumEvent.OreumSelected -> fetchOreumDetail(event.id)
            OreumEvent.RefreshRequested -> startObservation(forceRefresh = true)
        }
    }

    private fun startObservation(forceRefresh: Boolean = false) {
        if (!forceRefresh && observeJob != null) return

        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }

            interactor.observeOreumOverviews()
                .collectLatest { result ->
                    result
                        .onSuccess { overviews ->
                            val uiModels = overviews.toUiModels()
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    oreums = uiModels
                                )
                            }
                        }
                        .onFailure { throwable ->
                            setState { copy(isLoading = false, errorMessage = throwable.message) }
                            sendEffect {
                                OreumEffect.ShowError(
                                    throwable.message ?: "알 수 없는 오류 발생"
                                )
                            }
                        }
                }
        }
    }

    private fun fetchOreumDetail(oreumId: String) {
        viewModelScope.launch {
            interactor.getOreumOverview(oreumId)
                .onSuccess { overview ->
                    sendEffect { OreumEffect.NavigateToDetail(overview.id) }
                }
                .onFailure { throwable ->
                    sendEffect {
                        OreumEffect.ShowError(
                            throwable.message ?: "선택한 오름 정보를 불러오지 못했어요."
                        )
                    }
                }
        }
    }

    private fun List<OreumOverview>.toUiModels(): List<OreumUiModel> = map(uiMapper::map)
}

package com.jeong.feature.oreum.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.jeong.core.ui.viewmodel.BaseViewModel
import com.jeong.domain.usecase.GetOreumDetailUseCase
import com.jeong.domain.usecase.ObserveOreumsUseCase
import com.jeong.feature.oreum.presentation.model.OreumUiModel
import com.jeong.feature.oreum.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class OreumViewModel @Inject constructor(
    private val observeOreumsUseCase: ObserveOreumsUseCase,
    private val getOreumDetailUseCase: GetOreumDetailUseCase
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

            observeOreumsUseCase(Unit)
                .collectLatest { result ->
                    result
                        .onSuccess { oreums ->
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    oreums = oreums.toUiState()
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
            getOreumDetailUseCase(oreumId)
                .onSuccess { sendEffect { OreumEffect.NavigateToDetail(it.id) } }
                .onFailure { throwable ->
                    sendEffect {
                        OreumEffect.ShowError(
                            throwable.message ?: "선택한 오름 정보를 불러오지 못했어요."
                        )
                    }
                }
        }
    }

    private fun List<com.jeong.domain.model.Oreum>.toUiState(): List<OreumUiModel> =
        map { it.toUiModel() }
}

package com.jeong.jejuoreum.feature.oreum.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.feature.oreum.domain.OreumDetailInteractor
import com.jeong.jejuoreum.feature.oreum.domain.model.OreumStampRequest
import com.jeong.jejuoreum.feature.oreum.presentation.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.oreum.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val oreumDetailInteractor: OreumDetailInteractor,
    private val stateReducer: DetailStateReducer
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    sealed class DetailEvent {
        data object StampSuccess : DetailEvent()
        data class StampFailure(val message: String?) : DetailEvent()
    }

    private val _event = MutableSharedFlow<DetailEvent>()
    val event: SharedFlow<DetailEvent> = _event.asSharedFlow()

    init {
        loadLocationPermissionState()
    }

    fun initialize(oreum: OreumSummaryUiModel) {
        val oreumIdx = oreum.idx.toString()
        _uiState.update { stateReducer.initialize(it, oreum) }
        viewModelScope.launch {
            refreshOreumDetail(oreumIdx)
            refreshFavoriteStatus(oreumIdx)
            refreshStampStatus(oreumIdx)
            refreshReviews(oreumIdx)
        }
    }

    private suspend fun refreshOreumDetail(oreumIdx: String) {
        _uiState.update(stateReducer::onLoading)
        oreumDetailInteractor.fetchOreumDetail(oreumIdx)
            .map { it.toUiModel() }
            .onSuccess { detail ->
                _uiState.update { stateReducer.onDetailLoaded(it, detail) }
            }
            .onFailure { error ->
                _uiState.update { stateReducer.onDetailLoadFailed(it, error.message) }
            }
    }

    private suspend fun refreshFavoriteStatus(oreumIdx: String) {
        oreumDetailInteractor.fetchFavoriteStatus(oreumIdx)
            .onSuccess { isFavorite ->
                _uiState.update { stateReducer.onFavoriteStatusChanged(it, isFavorite) }
            }
            .onFailure(::handleError)
    }

    private suspend fun refreshStampStatus(oreumIdx: String) {
        oreumDetailInteractor.fetchStampStatus(oreumIdx)
            .onSuccess { hasStamp ->
                _uiState.update { stateReducer.onStampStatusChanged(it, hasStamp) }
            }.onFailure(::handleError)
    }

    private suspend fun refreshReviews(oreumIdx: String) {
        oreumDetailInteractor.fetchReviews(oreumIdx)
            .onSuccess { reviews ->
                _uiState.update { stateReducer.onReviewsLoaded(it, reviews) }
            }.onFailure(::handleError)
    }

    fun toggleFavorite() {
        val oreumIdx = _uiState.value.oreumDetail?.idx?.toString() ?: return
        viewModelScope.launch {
            val newIsFavorite = !_uiState.value.isFavorite
            oreumDetailInteractor.toggleFavorite(oreumIdx, newIsFavorite)
                .onSuccess { newTotal ->
                    _uiState.update {
                        stateReducer.onFavoriteToggled(it, newIsFavorite, newTotal)
                    }
                }
                .onFailure(::handleError)
        }
    }

    fun loadReviews() {
        val oreumIdx = _uiState.value.oreumDetail?.idx?.toString() ?: return
        viewModelScope.launch { refreshReviews(oreumIdx) }
    }

    fun stampOreum() {
        val oreum = _uiState.value.oreumDetail ?: return
        viewModelScope.launch {
            oreumDetailInteractor.tryStamp(
                OreumStampRequest(
                    oreumIdx = oreum.idx.toString(),
                    oreumName = oreum.oreumKname,
                    latitude = oreum.y,
                    longitude = oreum.x
                )
            ).onSuccess {
                refreshStampStatus(oreum.idx.toString())
                _event.emit(DetailEvent.StampSuccess)
            }.onFailure { error ->
                _event.emit(DetailEvent.StampFailure(error.message))
            }
        }
    }

    fun clearError() {
        _uiState.update(stateReducer::clearError)
    }

    fun onLocationPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            oreumDetailInteractor.updateLocationPermission(granted)
                .onSuccess {
                    _uiState.update {
                        stateReducer.onLocationPermissionChanged(it, granted)
                    }
                }
                .onFailure(::handleError)
        }
    }

    private fun loadLocationPermissionState() {
        viewModelScope.launch {
            oreumDetailInteractor.loadLocationPermissionState()
                .onSuccess { granted ->
                    _uiState.update {
                        stateReducer.onLocationPermissionChanged(it, granted)
                    }
                }.onFailure(::handleError)
        }
    }

    private fun handleError(error: Throwable) {
        _uiState.update { stateReducer.onError(it, error.message) }
    }
}

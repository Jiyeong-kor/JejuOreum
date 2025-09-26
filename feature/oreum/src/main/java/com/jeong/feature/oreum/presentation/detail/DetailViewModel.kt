package com.jeong.feature.oreum.presentation.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.usecase.ToggleFavoriteUseCase
import com.jeong.feature.oreum.R
import com.jeong.feature.oreum.domain.usecase.FetchOreumDetailUseCase
import com.jeong.feature.oreum.domain.usecase.GetOreumFavoriteStatusUseCase
import com.jeong.feature.oreum.domain.usecase.GetOreumReviewsUseCase
import com.jeong.feature.oreum.domain.usecase.GetOreumStampStatusUseCase
import com.jeong.feature.oreum.domain.usecase.TryStampUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val fetchOreumDetailUseCase: FetchOreumDetailUseCase,
    private val getOreumFavoriteStatusUseCase: GetOreumFavoriteStatusUseCase,
    private val getOreumStampStatusUseCase: GetOreumStampStatusUseCase,
    private val getOreumReviewsUseCase: GetOreumReviewsUseCase,
    private val tryStampUseCase: TryStampUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    sealed class DetailEvent {
        data object StampSuccess : DetailEvent()
        data class StampFailure(val message: String) : DetailEvent()
    }

    private val _event = MutableSharedFlow<DetailEvent>()
    val event: SharedFlow<DetailEvent> = _event.asSharedFlow()

    fun initialize(oreum: ResultSummary) {
        viewModelScope.launch {
            val oreumIdx = oreum.idx.toString()
            _uiState.update {
                it.copy(
                    oreumDetail = oreum,
                    isFavorite = oreum.userLiked,
                    hasStamp = oreum.userStamped,
                    errorMessage = null
                )
            }
            refreshOreumDetail(oreumIdx)
            refreshFavoriteStatus(oreumIdx)
            refreshStampStatus(oreumIdx)
            refreshReviews(oreumIdx)
        }
    }

    private suspend fun refreshOreumDetail(oreumIdx: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        fetchOreumDetailUseCase(oreumIdx)
            .onSuccess { detail ->
                _uiState.update {
                    it.copy(
                        oreumDetail = detail,
                        isFavorite = detail.userLiked,
                        hasStamp = detail.userStamped,
                        isLoading = false
                    )
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
    }

    private suspend fun refreshFavoriteStatus(oreumIdx: String) {
        getOreumFavoriteStatusUseCase(oreumIdx)
            .onSuccess { isFavorite ->
                _uiState.update { state ->
                    state.copy(
                        isFavorite = isFavorite,
                        oreumDetail = state.oreumDetail?.copy(userLiked = isFavorite)
                    )
                }
            }
            .onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
    }

    private suspend fun refreshStampStatus(oreumIdx: String) {
        getOreumStampStatusUseCase(oreumIdx)
            .onSuccess { hasStamp ->
                _uiState.update { state ->
                    state.copy(
                        hasStamp = hasStamp,
                        oreumDetail = state.oreumDetail?.copy(userStamped = hasStamp)
                    )
                }
            }
            .onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
    }

    private suspend fun refreshReviews(oreumIdx: String) {
        getOreumReviewsUseCase(oreumIdx)
            .onSuccess { reviews ->
                _uiState.update { it.copy(reviewList = reviews) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
    }

    fun toggleFavorite() {
        val oreumIdx = _uiState.value.oreumDetail?.idx?.toString() ?: return
        viewModelScope.launch {
            val newIsFavorite = !_uiState.value.isFavorite
            runCatching { toggleFavoriteUseCase(oreumIdx, newIsFavorite) }
                .onSuccess { newTotal ->
                    _uiState.update { state ->
                        state.copy(
                            isFavorite = newIsFavorite,
                            oreumDetail = state.oreumDetail?.copy(
                                userLiked = newIsFavorite,
                                totalFavorites = newTotal
                            )
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
        }
    }

    fun loadReviews() {
        val oreumIdx = _uiState.value.oreumDetail?.idx?.toString() ?: return
        viewModelScope.launch { refreshReviews(oreumIdx) }
    }

    fun stampOreum() {
        val oreum = _uiState.value.oreumDetail ?: return
        viewModelScope.launch {
            tryStampUseCase(
                oreumIdx = oreum.idx.toString(),
                oreumName = oreum.oreumKname,
                oreumLat = oreum.y,
                oreumLng = oreum.x
            ).onSuccess {
                refreshStampStatus(oreum.idx.toString())
                _event.emit(DetailEvent.StampSuccess)
            }.onFailure { error ->
                val message = error.message
                    ?: context.getString(R.string.oreum_unknown_error_message)
                _event.emit(DetailEvent.StampFailure(message))
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

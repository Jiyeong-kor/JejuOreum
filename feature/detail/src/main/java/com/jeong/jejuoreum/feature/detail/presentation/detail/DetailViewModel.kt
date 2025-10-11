package com.jeong.jejuoreum.feature.detail.presentation.detail

import com.jeong.jejuoreum.core.presentation.CommonBaseViewModel
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.detail.domain.OreumDetailInteractor
import com.jeong.jejuoreum.feature.detail.domain.model.OreumStampRequest
import com.jeong.jejuoreum.feature.detail.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val DEFAULT_ERROR_MESSAGE = "오류가 발생하였습니다."

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val oreumDetailInteractor: OreumDetailInteractor,
    private val stateReducer: DetailStateReducer,
) : CommonBaseViewModel<DetailUiState, DetailEvent, DetailEffect>() {

    private var initializedOreumId: String? = null

    override fun initialState(): DetailUiState = DetailUiState()

    override fun handleEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.Initialize -> initialize(event.oreum)
            DetailEvent.FavoriteClicked -> toggleFavorite()
            DetailEvent.StampRequested -> stampOreum()
            DetailEvent.ReviewsRequested -> refreshReviews()
            is DetailEvent.LocationPermissionResult -> updateLocationPermission(event.granted)
        }
    }

    private fun initialize(oreum: OreumSummaryUiModel) {
        val oreumIdx = oreum.idx.takeIf { it >= 0 }?.toString() ?: return
        if (initializedOreumId == oreumIdx) return
        initializedOreumId = oreumIdx

        setState { stateReducer.initialize(this, oreum) }
        loadLocationPermissionState()
        refreshDetail(oreumIdx)
        refreshFavoriteStatus(oreumIdx)
        refreshStampStatus(oreumIdx)
        refreshReviews()
    }

    private fun refreshDetail(oreumIdx: String) {
        launch {
            setState(stateReducer::onLoading)
            oreumDetailInteractor.fetchOreumDetail(oreumIdx)
                .map { it.toUiModel() }
                .onSuccess { detail ->
                    setState { stateReducer.onDetailLoaded(this, detail) }
                }
                .onFailure { error ->
                    setState { stateReducer.onDetailLoadFailed(this) }
                    sendError(error.message)
                }
        }
    }

    private fun refreshFavoriteStatus(oreumIdx: String) {
        launch {
            oreumDetailInteractor.fetchFavoriteStatus(oreumIdx)
                .onSuccess { isFavorite ->
                    setState { stateReducer.onFavoriteStatusChanged(this, isFavorite) }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun refreshStampStatus(oreumIdx: String) {
        launch {
            oreumDetailInteractor.fetchStampStatus(oreumIdx)
                .onSuccess { hasStamp ->
                    setState { stateReducer.onStampStatusChanged(this, hasStamp) }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun refreshReviews() {
        val oreumIdx = currentOreumIdx() ?: return
        launch {
            oreumDetailInteractor.fetchReviews(oreumIdx)
                .onSuccess { reviews ->
                    setState { stateReducer.onReviewsLoaded(this, reviews) }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun toggleFavorite() {
        val oreumIdx = currentOreumIdx() ?: return
        val newIsFavorite = !currentState.isFavorite
        launch {
            oreumDetailInteractor.toggleFavorite(oreumIdx, newIsFavorite)
                .onSuccess { newTotal ->
                    setState {
                        stateReducer.onFavoriteToggled(this, newIsFavorite, newTotal)
                    }
                    sendEffect {
                        DetailEffect.FavoriteStatusChanged(
                            oreumIdx = oreumIdx,
                            isFavorite = newIsFavorite,
                        )
                    }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun stampOreum() {
        val oreum = currentState.oreumDetail ?: return
        launch {
            oreumDetailInteractor.tryStamp(
                OreumStampRequest(
                    oreumIdx = oreum.idx.toString(),
                    oreumName = oreum.oreumKname,
                    latitude = oreum.y,
                    longitude = oreum.x,
                )
            ).onSuccess {
                refreshStampStatus(oreum.idx.toString())
                sendEffect { DetailEffect.StampCompleted(oreum.idx.toString()) }
            }.onFailure { error ->
                sendError(error.message)
            }
        }
    }

    private fun updateLocationPermission(granted: Boolean) {
        launch {
            oreumDetailInteractor.updateLocationPermission(granted)
                .onSuccess {
                    setState { stateReducer.onLocationPermissionChanged(this, granted) }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun loadLocationPermissionState() {
        launch {
            oreumDetailInteractor.loadLocationPermissionState()
                .onSuccess { granted ->
                    setState { stateReducer.onLocationPermissionChanged(this, granted) }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun currentOreumIdx(): String? = currentState.oreumDetail?.idx?.toString()

    private fun sendError(message: String?) {
        sendEffect {
            DetailEffect.ShowMessage(message ?: DEFAULT_ERROR_MESSAGE)
        }
    }
}

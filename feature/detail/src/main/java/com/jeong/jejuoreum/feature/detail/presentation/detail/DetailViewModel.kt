package com.jeong.jejuoreum.feature.detail.presentation.detail

import com.jeong.jejuoreum.core.presentation.CommonBaseViewModel
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumDetailUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.RefreshOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.TryStampUseCase
import com.jeong.jejuoreum.domain.review.usecase.FetchReviewsUseCase
import com.jeong.jejuoreum.domain.user.usecase.GetOreumFavoriteStatusUseCase
import com.jeong.jejuoreum.domain.user.usecase.GetOreumStampStatusUseCase
import com.jeong.jejuoreum.domain.user.usecase.IsLocationPermissionGrantedUseCase
import com.jeong.jejuoreum.domain.user.usecase.ToggleFavoriteUseCase
import com.jeong.jejuoreum.domain.user.usecase.UpdateLocationPermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getOreumDetailUseCase: GetOreumDetailUseCase,
    private val getOreumFavoriteStatusUseCase: GetOreumFavoriteStatusUseCase,
    private val getOreumStampStatusUseCase: GetOreumStampStatusUseCase,
    private val fetchReviewsUseCase: FetchReviewsUseCase,
    private val tryStampUseCase: TryStampUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val refreshOreumSummariesUseCase: RefreshOreumSummariesUseCase,
    private val isLocationPermissionGrantedUseCase: IsLocationPermissionGrantedUseCase,
    private val updateLocationPermissionUseCase: UpdateLocationPermissionUseCase,
    private val stateReducer: DetailStateReducer,
    private val detailMessageProvider: DetailMessageProvider, // Refactored to apply DIP by injecting message abstraction.
    @Named("ioDispatcher") ioDispatcher: CoroutineDispatcher,
) : CommonBaseViewModel<DetailUiState, DetailEvent, DetailEffect>(ioDispatcher) {

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

    override fun buildErrorEffect(message: String): DetailEffect =
        DetailEffect.ShowMessage(message.ifBlank { detailMessageProvider.defaultError() })

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
            getOreumDetailUseCase(oreumIdx)
                .map { it.toUiModel(currentState.oreumDetail) }
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
            getOreumFavoriteStatusUseCase(oreumIdx)
                .onSuccess { isFavorite ->
                    setState { stateReducer.onFavoriteStatusChanged(this, isFavorite) }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun refreshStampStatus(oreumIdx: String) {
        launch {
            getOreumStampStatusUseCase(oreumIdx)
                .onSuccess { hasStamp ->
                    setState { stateReducer.onStampStatusChanged(this, hasStamp) }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun refreshReviews() {
        val oreumIdx = currentOreumIdx() ?: return
        launch {
            fetchReviewsUseCase(oreumIdx)
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
            toggleFavoriteUseCase(oreumIdx, newIsFavorite)
                .mapCatching { newTotal ->
                    refreshOreumSummariesUseCase()
                        .map { newTotal }
                        .getOrElse { throw it }
                }
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
            tryStampUseCase(
                oreumIdx = oreum.idx.toString(),
                oreumName = oreum.oreumKname,
                oreumLat = oreum.y,
                oreumLng = oreum.x,
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
            updateLocationPermissionUseCase(granted)
                .onSuccess {
                    setState { stateReducer.onLocationPermissionChanged(this, granted) }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun loadLocationPermissionState() {
        launch {
            isLocationPermissionGrantedUseCase()
                .onSuccess { granted ->
                    setState { stateReducer.onLocationPermissionChanged(this, granted) }
                }
                .onFailure { error -> sendError(error.message) }
        }
    }

    private fun currentOreumIdx(): String? = currentState.oreumDetail?.idx?.toString()

    private fun sendError(message: String?) {
        sendEffect {
            DetailEffect.ShowMessage(message ?: detailMessageProvider.defaultError())
        }
    }

    private fun Oreum.toUiModel(previous: OreumSummaryUiModel?): OreumSummaryUiModel {
        val base = previous ?: OreumSummaryUiModel()
        return base.copy(
            idx = id.toIntOrNull() ?: base.idx,
            oreumKname = name.ifBlank { base.oreumKname },
            oreumEname = if (base.oreumEname.isNotBlank()) base.oreumEname else name,
            oreumAddr = location.ifBlank { base.oreumAddr },
            explain = description.ifBlank { base.explain },
            oreumAltitu = elevation.takeIf { it != 0.0 } ?: base.oreumAltitu,
            imgPath = thumbnailUrl.ifBlank { base.imgPath },
            userLiked = isFavorite,
        )
    }
}

package com.jeong.jejuoreum.feature.profile.presentation.favorite

import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.common.result.ResultResource
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.usecase.LoadOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveFavoriteOreumsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.RefreshOreumSummariesUseCase
import com.jeong.jejuoreum.domain.user.usecase.ToggleFavoriteUseCase
import com.jeong.jejuoreum.feature.profile.presentation.ProfileBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@HiltViewModel
class MyFavoriteViewModel @Inject constructor(
    private val loadOreumSummariesUseCase: LoadOreumSummariesUseCase,
    private val observeFavoriteOreumsUseCase: ObserveFavoriteOreumsUseCase,
    private val refreshOreumSummariesUseCase: RefreshOreumSummariesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val errorMessageProvider: MyFavoriteErrorMessageProvider,
    private val uiMapper: MyFavoriteUiMapper,
    @Named("ioDispatcher") ioDispatcher: CoroutineDispatcher,
) : ProfileBaseViewModel(ioDispatcher) {

    private var observeJob: Job? = null

    init {
        onEvent(MyFavoriteUiEvent.Initialize)
    }

    override fun initialState(): MyFavoriteUiState = MyFavoriteUiState()

    override fun onCleared() {
        observeJob?.cancel()
        observeJob = null
        super.onCleared()
    }

    override fun handleEvent(event: MyFavoriteUiEvent) {
        when (event) {
            MyFavoriteUiEvent.Initialize -> observeFavorites()
            is MyFavoriteUiEvent.FavoriteToggled -> toggleFavorite(event.oreumIdx, event.currentlyLiked)
            MyFavoriteUiEvent.ErrorConsumed -> setState { copy(errorMessage = null) }
        }
    }

    override fun handleError(t: Throwable) {
        handleFailure(t)
    }

    private fun observeFavorites() {
        if (observeJob != null) return

        observeJob = launch {
            setState { copy(isLoading = true, errorMessage = null) }
            val loadResult = loadOreumSummariesUseCase()
            if (loadResult.isFailure) {
                val error = loadResult.exceptionOrNull()
                handleFailure(error)
                observeJob = null
                return@launch
            }

            observeFavoriteOreumsUseCase().collectLatest { result ->
                handleResult(result)
            }
        }
    }

    private fun handleResult(result: ResultResource<List<ResultSummary>>) {
        result.fold(
            onSuccess = { resource ->
                when (resource) {
                    Resource.Loading -> setState { copy(isLoading = true, errorMessage = null) }
                    is Resource.Success -> updateFavorites(resource.data)
                    is Resource.Error -> handleFailure(resource.throwable)
                }
            },
            onFailure = ::handleFailure,
        )
    }

    private fun updateFavorites(favorites: List<ResultSummary>) {
        setState {
            copy(
                isLoading = false,
                errorMessage = null,
                favorites = uiMapper.mapToUiModels(favorites),
            )
        }
    }

    private fun toggleFavorite(oreumIdx: String, currentlyLiked: Boolean) {
        launch {
            val toggleResult = toggleFavoriteUseCase(oreumIdx, !currentlyLiked)
            toggleResult
                .onFailure(::handleFailure)
                .onSuccess {
                    refreshOreumSummariesUseCase()
                        .onFailure(::handleFailure)
                }
        }
    }

    private fun handleFailure(throwable: Throwable?) {
        val message = errorMessageProvider.favoriteLoadingFailed(throwable)
        Timber.w(throwable, "Failed to process favorite oreum state")
        setState { copy(isLoading = false, errorMessage = message) }
        sendErrorEffect(message)
    }
}

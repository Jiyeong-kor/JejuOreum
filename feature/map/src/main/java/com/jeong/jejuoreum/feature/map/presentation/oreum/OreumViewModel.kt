package com.jeong.jejuoreum.feature.map.presentation.oreum

import com.jeong.jejuoreum.core.common.UiText
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.common.result.ResourceError
import com.jeong.jejuoreum.core.common.viewmodel.CommonBaseViewModel
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumSummariesUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.LoadOreumDetailUseCase
import com.jeong.jejuoreum.feature.map.R
import com.jeong.jejuoreum.feature.map.presentation.mapper.OreumUiMapper
import com.jeong.jejuoreum.feature.map.presentation.model.OreumUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class OreumViewModel @Inject constructor(
    private val getOreumSummariesUseCase: GetOreumSummariesUseCase,
    private val loadOreumDetailUseCase: LoadOreumDetailUseCase,
    private val uiMapper: OreumUiMapper,
    @Named("ioDispatcher") ioDispatcher: CoroutineDispatcher,
) : CommonBaseViewModel<OreumUiState, OreumEvent, OreumEffect>(ioDispatcher) {

    private var observeJob: Job? = null

    init {
        onEvent(OreumEvent.ScreenInitialized)
    }

    override fun initialState(): OreumUiState = OreumUiState()

    override fun onCleared() {
        observeJob?.cancel()
        observeJob = null
        super.onCleared()
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
        observeJob = launch {
            setState { copy(isLoading = true, errorMessage = null) }

            getOreumSummariesUseCase()
                .collectLatest { resource ->
                    when (resource) {
                        Resource.Loading -> setState {
                            copy(isLoading = true, errorMessage = null)
                        }

                        is Resource.Success -> {
                            val uiModels = resource.data.toUiModels()
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    oreums = uiModels,
                                )
                            }
                        }

                        is Resource.Error -> handleFailure(resource.error)
                    }
                }
        }
    }

    override fun buildErrorEffect(message: UiText): OreumEffect = OreumEffect.ShowError(message)

    private fun handleFailure(error: ResourceError) {
        val message = when (error) {
            ResourceError.Network -> UiText.StringResource(R.string.error_network_unavailable)
            is ResourceError.Api -> error.message?.let(UiText::DynamicString)
                ?: UiText.StringResource(R.string.error_failed_to_load_oreum_data)
            is ResourceError.NotFound -> UiText.StringResource(R.string.error_oreum_not_found)
            ResourceError.Unauthorized -> UiText.StringResource(R.string.error_authentication_required)
            is ResourceError.Unknown -> error.throwable.message?.let(UiText::DynamicString)
                ?: UiText.StringResource(R.string.error_unknown)
        }
        setState { copy(isLoading = false, errorMessage = message) }
        sendEffect { OreumEffect.ShowError(message) }
    }

    private fun fetchOreumDetail(oreumId: String) {
        launch {
            loadOreumDetailUseCase(oreumId)
                .onSuccess { oreum ->
                    sendEffect { OreumEffect.NavigateToDetail(oreum.id) }
                }
                .onFailure { throwable ->
                    val message = throwable.message?.let(UiText::DynamicString)
                        ?: UiText.StringResource(R.string.error_failed_to_load_selected_oreum)
                    sendEffect { OreumEffect.ShowError(message) }
                }
        }
    }

    private fun List<ResultSummary>.toUiModels(): List<OreumUiModel> = map(uiMapper::map)
}

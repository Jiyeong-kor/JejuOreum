package com.jeong.jejuoreum.feature.splash.presentation

import com.jeong.jejuoreum.core.common.UiText
import com.jeong.jejuoreum.core.common.viewmodel.CommonBaseViewModel
import com.jeong.jejuoreum.feature.splash.R
import com.jeong.jejuoreum.feature.splash.domain.PrepareSplashInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prepareSplashInteractor: PrepareSplashInteractor,
    @Named("ioDispatcher") ioDispatcher: CoroutineDispatcher,
) : CommonBaseViewModel<SplashUiState, SplashUiEvent, SplashUiEffect>(ioDispatcher) {

    override fun initialState(): SplashUiState = SplashUiState()

    override fun handleEvent(event: SplashUiEvent) {
        when (event) {
            SplashUiEvent.Initialize -> initialize()
            SplashUiEvent.ErrorConsumed -> clearError()
        }
    }

    private fun initialize() {
        launch {
            setState { copy(isLoading = true, errorMessage = null) }

            val result = prepareSplashInteractor()
            result.fold(
                onSuccess = { destination ->
                    setState { copy(isLoading = false) }
                    sendEffect { SplashUiEffect.NavigateTo(destination) }
                },
                onFailure = { throwable ->
                    Timber.e(throwable, "❌ Splash 준비 실패")
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = throwable.message?.let(UiText::DynamicString)
                                ?: UiText.StringResource(R.string.splash_error_generic),
                        )
                    }
                }
            )
        }
    }

    private fun clearError() {
        setState { copy(errorMessage = null) }
    }
}

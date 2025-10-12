package com.jeong.jejuoreum.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import com.jeong.jejuoreum.core.common.viewmodel.UiContract
import com.jeong.jejuoreum.core.presentation.R
import com.jeong.jejuoreum.core.presentation.UiText
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel implementation that standardizes state, event, and effect flows across features.
 */
abstract class CommonBaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel(), UiContract<S, E, F> {

    protected val _uiState: MutableStateFlow<S> = MutableStateFlow(initialState())
    override val uiState: StateFlow<S> = _uiState.asStateFlow()

    protected val _effect: MutableSharedFlow<F> = MutableSharedFlow(replay = 0)
    override val effect: SharedFlow<F> = _effect.asSharedFlow()

    protected val currentState: S
        get() = _uiState.value

    protected abstract fun initialState(): S

    protected abstract fun handleEvent(event: E)

    override fun onEvent(event: E) {
        handleEvent(event)
    }

    protected fun setState(reducer: S.() -> S) {
        _uiState.update { it.reducer() }
    }

    protected fun sendEffect(builder: () -> F) {
        viewModelScope.launch {
            _effect.emit(builder())
        }
    }

    protected fun launch(
        dispatcher: CoroutineDispatcher = ioDispatcher,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch(dispatcher) {
        try {
            block()
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            handleError(throwable)
        }
    }

    protected open fun handleError(t: Throwable) {
        val message = when (t) {
            is IOException -> UiText.StringResource(R.string.core_error_network)
            else -> UiText.StringResource(R.string.core_error_unknown)
        }
        sendErrorEffect(message)
    }

    protected open fun buildErrorEffect(message: UiText): F? = null

    protected fun sendErrorEffect(message: UiText) {
        val effect = buildErrorEffect(message) ?: return
        viewModelScope.launch { _effect.emit(effect) }
    }
}

package com.jeong.jejuoreum.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

public abstract class CommonBaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(), UiContract<S, E, F> {

    protected val _uiState: MutableStateFlow<S> = MutableStateFlow(initialState())
    override val uiState: StateFlow<S> = _uiState.asStateFlow()

    protected val _effect: MutableSharedFlow<F> = MutableSharedFlow(replay = 0)
    override val effect: Flow<F> = _effect.asSharedFlow()

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
            is IOException -> "네트워크 오류가 발생했습니다."
            else -> "알 수 없는 오류가 발생했습니다."
        }
        emitErrorEffect(message)
    }

    protected open fun createErrorEffect(message: String): F? = null

    protected fun emitErrorEffect(message: String) {
        val effect = createErrorEffect(message) ?: return
        viewModelScope.launch { _effect.emit(effect) }
    }
}

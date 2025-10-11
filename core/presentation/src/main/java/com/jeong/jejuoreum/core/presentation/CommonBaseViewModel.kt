package com.jeong.jejuoreum.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

public abstract class CommonBaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(), UiContract<S, E, F> {

    protected val mutableUiState: MutableStateFlow<S> = MutableStateFlow(initialState())
    override val uiState: StateFlow<S> = mutableUiState.asStateFlow()

    protected val mutableEffect: MutableSharedFlow<F> = MutableSharedFlow(replay = 0)
    override val effect: SharedFlow<F> = mutableEffect.asSharedFlow()

    protected val currentState: S
        get() = mutableUiState.value

    protected abstract fun initialState(): S

    protected abstract fun handleEvent(event: E)

    protected open fun handleError(throwable: Throwable) {}

    override fun onEvent(event: E) {
        handleEvent(event)
    }

    protected fun setState(reducer: S.() -> S) {
        mutableUiState.update { it.reducer() }
    }

    protected fun sendEffect(builder: () -> F) {
        viewModelScope.launch {
            mutableEffect.emit(builder())
        }
    }

    protected fun launch(
        dispatcher: CoroutineDispatcher = ioDispatcher,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch(dispatcher) {
        try {
            block()
        } catch (throwable: Throwable) {
            handleError(throwable)
        }
    }
}

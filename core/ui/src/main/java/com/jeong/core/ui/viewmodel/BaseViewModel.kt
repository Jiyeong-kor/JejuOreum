package com.jeong.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.core.ui.state.UiEffect
import com.jeong.core.ui.state.UiEvent
import com.jeong.core.ui.state.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<EVENT : UiEvent, EFFECT : UiEffect, STATE : UiState>(
    initialState: STATE,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _effect = Channel<EFFECT>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected fun setState(reducer: STATE.() -> STATE) {
        _state.value = _state.value.reducer()
    }

    protected fun sendEffect(builder: () -> EFFECT) {
        viewModelScope.launch { _effect.send(builder()) }
    }

    fun onEvent(event: EVENT) {
        handleEvent(event)
    }

    protected abstract fun handleEvent(event: EVENT)
}

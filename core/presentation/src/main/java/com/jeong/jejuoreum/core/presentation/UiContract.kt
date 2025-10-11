package com.jeong.jejuoreum.core.presentation

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

public interface UiContract<S : UiState, E : UiEvent, F : UiEffect> {
    public fun onEvent(event: E)
    public val uiState: StateFlow<S>
    public val effect: Flow<F>
}

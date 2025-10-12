package com.jeong.jejuoreum.core.common.viewmodel

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract describing the reactive communication between UI and ViewModel layers.
 */
interface UiContract<S : UiState, E : UiEvent, F : UiEffect> {
    /** Dispatches an incoming UI event to the ViewModel. */
    fun onEvent(event: E)

    /** Exposes the stream of UI state updates that the UI layer observes. */
    val uiState: StateFlow<S>

    /** Emits one-off side effects such as navigation or toast messages. */
    val effect: SharedFlow<F>
}

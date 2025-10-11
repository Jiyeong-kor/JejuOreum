package com.jeong.jejuoreum.core.presentation

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * 화면과 ViewModel 사이에서 주고받는 흐름을 정리한 계약
 * UI 계층은 이벤트를 전달하고 상태와 부수 효과를 구독하면서 일관된 패턴을 유지
 */
public interface UiContract<S : UiState, E : UiEvent, F : UiEffect> {
    /** UI에서 전달한 상호작용을 처리하기 위해 호출 */
    public fun onEvent(event: E)

    /**
     * 화면에서 구독하는 상태 스트림으로, render 직전에 최신 상태를 보장
     */
    public val uiState: StateFlow<S>

    /** 일시적인 알림이나 네비게이션 등 단발성 처리를 전달 */
    public val effect: Flow<F>
}

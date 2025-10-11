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

/**
 * 프레젠테이션 계층에서 공통으로 활용하는 ViewModel 기본 구현
 * 상태, 이벤트, 부수 효과 흐름을 표준화하여 각 기능 모듈이 일관되게 동작하게 함
 */
public abstract class CommonBaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(), UiContract<S, E, F> {

    /** 실제 UI가 구독하는 상태 스트림을 보유하고 있는 내부 저장소 */
    protected val _uiState: MutableStateFlow<S> = MutableStateFlow(initialState())
    override val uiState: StateFlow<S> = _uiState.asStateFlow()

    /** 단발성 알림을 전달하기 위해 사용되는 공유 플로우 */
    protected val _effect: MutableSharedFlow<F> = MutableSharedFlow(replay = 0)
    override val effect: Flow<F> = _effect.asSharedFlow()

    /** 비즈니스 로직에서 즉시 접근할 수 있도록 현 상태를 노출 */
    protected val currentState: S
        get() = _uiState.value

    /** 초기 화면 진입 시 사용할 상태를 정의 */
    protected abstract fun initialState(): S

    /**
     * UI에서 전달된 이벤트를 구체적으로 처리
     * 하위 클래스가 when 표현식 등을 이용해 세부 로직을 구현
     */
    protected abstract fun handleEvent(event: E)

    override fun onEvent(event: E) {
        handleEvent(event)
    }

    /**
     * 상태 객체를 불변 복사 패턴으로 갱신
     * reducer는 현재 상태를 기반으로 새로운 상태를 만들어 반환
     */
    protected fun setState(reducer: S.() -> S) {
        _uiState.update { it.reducer() }
    }

    /** UI에 노출해야 하는 단발성 이벤트를 안전하게 발행 */
    protected fun sendEffect(builder: () -> F) {
        viewModelScope.launch {
            _effect.emit(builder())
        }
    }

    /**
     * 비동기 작업을 실행하면서 공통적인 에러 처리를 적용
     * 기본적으로 IO Dispatcher를 사용하지만 필요에 따라 재정의
     */
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

    /**
     * 공통 에러 메시지를 생성해 effect로 전달
     * 하위 클래스에서 재정의하면 기능별 메시지를 커스터마이징
     */
    protected open fun handleError(t: Throwable) {
        val message = when (t) {
            is IOException -> "네트워크 오류가 발생했습니다."
            else -> "알 수 없는 오류가 발생했습니다."
        }
        emitErrorEffect(message)
    }

    /** 에러 메시지에 대응하는 effect를 만들어 반환 */
    protected open fun createErrorEffect(message: String): F? = null

    /** 공통 에러 effect를 발행하여 UI가 적절히 대응 */
    protected fun emitErrorEffect(message: String) {
        val effect = createErrorEffect(message) ?: return
        viewModelScope.launch { _effect.emit(effect) }
    }
}

package com.jeong.jejuoreum.core.presentation.state

/**
 * UI 상태가 주어진 변화에 어떻게 반응하는지를 정의하는 최소한의 인터페이스
 *
 * 상태 변경 로직을 별도의 reducer 클래스로 분리함으로써 모듈 간에 동일한 패턴을 재사용하고
 * ViewModel을 간결하게 유지하며 상태 전이를 독립적으로 단위 테스트하기 쉽게 함
 */

fun interface StateReducer<S, in C> {
    fun reduce(current: S, change: C): S
}

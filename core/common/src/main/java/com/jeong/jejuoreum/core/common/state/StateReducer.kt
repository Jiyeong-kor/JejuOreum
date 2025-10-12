package com.jeong.jejuoreum.core.common.state

/**
 * Describes how a UI state should react to a particular change or event.
 */
fun interface StateReducer<S, in C> {
    fun reduce(current: S, change: C): S
}

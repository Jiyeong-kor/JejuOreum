package com.jeong.feature.oreum.presentation.list

sealed interface ListEvent {
    data object StampSuccess : ListEvent
    data class StampFailure(val reason: String?) : ListEvent
    data class LoadFailed(val reason: String?) : ListEvent
}

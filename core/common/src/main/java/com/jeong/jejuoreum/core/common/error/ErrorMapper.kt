package com.jeong.jejuoreum.core.common.error

fun Throwable.toDomainError(): DomainError = when (this) {
    is DomainError -> this
    else -> DomainError.Unknown(this)
}

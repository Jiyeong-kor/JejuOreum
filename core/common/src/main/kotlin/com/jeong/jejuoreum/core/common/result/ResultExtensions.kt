package com.jeong.jejuoreum.core.common.result

import com.jeong.jejuoreum.core.common.error.toDomainError

inline fun <T> Result<T>.mapFailure(transform: (Throwable) -> Throwable): Result<T> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = { throwable -> Result.failure(transform(throwable)) }
    )

fun <T> Result<T>.mapToDomainError(): Result<T> = mapFailure(Throwable::toDomainError)

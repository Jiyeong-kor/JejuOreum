package com.jeong.jejuoreum.core.common.result

sealed interface JejuOreumResult<out T> {
    data class Success<T>(val value: T) : JejuOreumResult<T>
    data class Error(val throwable: Throwable) : JejuOreumResult<Nothing>

    companion object {
        inline fun <T> of(block: () -> T): JejuOreumResult<T> =
            try {
                Success(block())
            } catch (error: Throwable) {
                Error(error)
            }
    }
}

inline fun <T, R> JejuOreumResult<T>.map(transform: (T) -> R): JejuOreumResult<R> = when (this) {
    is JejuOreumResult.Error -> this
    is JejuOreumResult.Success -> JejuOreumResult.Success(transform(value))
}

inline fun <T> JejuOreumResult<T>.onSuccess(action: (T) -> Unit): JejuOreumResult<T> {
    if (this is JejuOreumResult.Success) {
        action(value)
    }
    return this
}

inline fun <T> JejuOreumResult<T>.onError(action: (Throwable) -> Unit): JejuOreumResult<T> {
    if (this is JejuOreumResult.Error) {
        action(throwable)
    }
    return this
}

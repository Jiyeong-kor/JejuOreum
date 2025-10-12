package com.jeong.jejuoreum.core.common.result

/**
 * Represents a value with its loading status.
 */
sealed interface Resource<out T> {

    data object Loading : Resource<Nothing>

    data class Success<T>(val data: T) : Resource<T>

    data class Error(val error: ResourceError) : Resource<Nothing>

    val isLoading: Boolean
        get() = this is Loading

    val dataOrNull: T?
        get() = (this as? Success<T>)?.data

    val errorOrNull: ResourceError?
        get() = (this as? Error)?.error
}

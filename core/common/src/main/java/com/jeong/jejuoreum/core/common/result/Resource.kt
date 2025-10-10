package com.jeong.jejuoreum.core.common.result

import com.jeong.jejuoreum.core.common.error.DomainError

sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>

    data class Success<T>(val data: T) : Resource<T>

    data class Error(val error: DomainError) : Resource<Nothing>
}

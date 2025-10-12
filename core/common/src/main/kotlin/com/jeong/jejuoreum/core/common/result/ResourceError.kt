package com.jeong.jejuoreum.core.common.result

/**
 * Represents an error emitted from a data stream.
 */
sealed interface ResourceError {

    data object Network : ResourceError

    data class Api(
        val code: Int?,
        val message: String? = null,
    ) : ResourceError

    data class NotFound(val id: String? = null) : ResourceError

    data object Unauthorized : ResourceError

    data class Unknown(val throwable: Throwable) : ResourceError
}

package com.jeong.jejuoreum.core.common.error

import com.jeong.jejuoreum.core.common.result.ResourceError
import java.io.IOException
import retrofit2.HttpException

fun Throwable.toDomainError(): DomainError = when (this) {
    is DomainError -> this
    else -> DomainError.Unknown(this)
}

fun Throwable.toResourceError(): ResourceError = when (this) {
    is DomainError -> this.toResourceError()
    is IOException -> ResourceError.Network
    is HttpException -> ResourceError.Api(code(), message())
    else -> ResourceError.Unknown(this)
}

fun DomainError.toResourceError(): ResourceError = when (this) {
    is DomainError.NotFound -> ResourceError.NotFound(resourceId)
    DomainError.AuthenticationRequired -> ResourceError.Unauthorized
    DomainError.LocationPermissionRequired -> ResourceError.Unknown(this)
    DomainError.LocationUnavailable -> ResourceError.Unknown(this)
    is DomainError.DistanceTooFar -> ResourceError.Unknown(this)
    is DomainError.Unknown -> ResourceError.Unknown(throwable)
}

package com.jeong.jejuoreum.core.common.error

import kotlin.math.roundToInt

sealed class DomainError(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {

    data class NotFound(val resourceId: String) : DomainError(
        message = "${resourceId} 번 리소스 아이디를 찾을 수 없습니다."
    )

    data object AuthenticationRequired : DomainError(
        message = "로그인이 필요합니다."
    )

    data object LocationPermissionRequired : DomainError(
        message = "위치 권한이 필요합니다."
    )

    data object LocationUnavailable : DomainError(
        message = "현재 위치를 확인할 수 없어요."
    )

    data class DistanceTooFar(val distanceMeters: Float) : DomainError(
        message = "Stamp 지역과의 거리가 ${distanceMeters.roundToInt()}m 입니다."
    )

    data class Unknown(val throwable: Throwable) : DomainError(
        message = throwable.message,
        cause = throwable
    )
}

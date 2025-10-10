package com.jeong.jejuoreum.core.common.error

sealed class DomainError(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {

    data class NotFound(val resourceId: String) : DomainError(
        message = "${resourceId} 번 리소스 아이디를 찾을 수 없습니다."
    )

    data object AuthenticationRequired : DomainError(
        message = "로그인이 필요합니다."
    )

    data class Unknown(val throwable: Throwable) : DomainError(
        message = throwable.message,
        cause = throwable
    )
}

package com.jeong.domain.model

enum class NicknameValidationError {
    TOO_SHORT,
    TOO_LONG,
    INVALID_CHARACTERS,
}

sealed class NicknameValidationResult(open val value: String) {
    data class Empty(override val value: String = "") : NicknameValidationResult(value)
    data class Invalid(
        override val value: String,
        val reason: NicknameValidationError,
    ) : NicknameValidationResult(value)

    data class Valid(override val value: String) : NicknameValidationResult(value)
}

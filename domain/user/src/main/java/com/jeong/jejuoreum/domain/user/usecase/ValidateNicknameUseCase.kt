package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.model.NicknameValidationError
import com.jeong.jejuoreum.domain.user.model.NicknameValidationResult

class ValidateNicknameUseCase {
    operator fun invoke(input: String): NicknameValidationResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return NicknameValidationResult.Empty(trimmed)
        }
        if (trimmed.length < MIN_LENGTH) {
            return NicknameValidationResult.Invalid(trimmed, NicknameValidationError.TOO_SHORT)
        }
        if (trimmed.length > MAX_LENGTH) {
            return NicknameValidationResult.Invalid(trimmed, NicknameValidationError.TOO_LONG)
        }
        if (!NICKNAME_REGEX.matches(trimmed)) {
            return NicknameValidationResult.Invalid(
                trimmed,
                NicknameValidationError.INVALID_CHARACTERS
            )
        }
        return NicknameValidationResult.Valid(trimmed)
    }

    companion object {
        private const val MIN_LENGTH = 3
        private const val MAX_LENGTH = 15
        private val NICKNAME_REGEX = Regex("^[가-힣a-zA-Z0-9]+$")
    }
}

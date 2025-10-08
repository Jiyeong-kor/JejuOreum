package com.jeong.jejuoreum.feature.onboarding.domain

import com.jeong.jejuoreum.domain.user.model.NicknameValidationResult

fun interface NicknameValidator {
    fun validate(input: String): NicknameValidationResult
}

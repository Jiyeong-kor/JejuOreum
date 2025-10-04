package com.jeong.feature.join.domain

import com.jeong.domain.model.NicknameValidationResult

fun interface NicknameValidator {
    fun validate(input: String): NicknameValidationResult
}

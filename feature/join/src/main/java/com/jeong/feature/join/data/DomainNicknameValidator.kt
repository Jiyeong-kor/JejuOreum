package com.jeong.feature.join.data

import com.jeong.domain.usecase.ValidateNicknameUseCase
import com.jeong.feature.join.domain.NicknameValidator
import javax.inject.Inject

class DomainNicknameValidator @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : NicknameValidator {
    override fun validate(input: String) = validateNicknameUseCase(input)
}

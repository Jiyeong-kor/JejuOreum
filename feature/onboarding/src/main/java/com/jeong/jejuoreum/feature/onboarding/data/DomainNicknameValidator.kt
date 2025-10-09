package com.jeong.jejuoreum.feature.join.data

import com.jeong.jejuoreum.domain.user.usecase.ValidateNicknameUseCase
import com.jeong.jejuoreum.feature.join.domain.NicknameValidator
import javax.inject.Inject

class DomainNicknameValidator @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : NicknameValidator {
    override fun validate(input: String) = validateNicknameUseCase(input)
}

package com.jeong.feature.join.data

import com.jeong.domain.usecase.CheckNicknameAvailabilityUseCase
import com.jeong.feature.join.domain.NicknameAvailabilityChecker
import javax.inject.Inject

class DomainNicknameAvailabilityChecker @Inject constructor(
    private val checkNicknameAvailabilityUseCase: CheckNicknameAvailabilityUseCase,
) : NicknameAvailabilityChecker {
    override suspend fun check(nickname: String) =
        checkNicknameAvailabilityUseCase(nickname)
}

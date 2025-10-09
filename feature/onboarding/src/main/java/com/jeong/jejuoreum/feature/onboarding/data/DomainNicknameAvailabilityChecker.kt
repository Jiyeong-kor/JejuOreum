package com.jeong.jejuoreum.feature.join.data

import com.jeong.jejuoreum.domain.user.usecase.CheckNicknameAvailabilityUseCase
import com.jeong.jejuoreum.feature.join.domain.NicknameAvailabilityChecker
import javax.inject.Inject

class DomainNicknameAvailabilityChecker @Inject constructor(
    private val checkNicknameAvailabilityUseCase: CheckNicknameAvailabilityUseCase,
) : NicknameAvailabilityChecker {
    override suspend fun check(nickname: String) =
        checkNicknameAvailabilityUseCase(nickname)
}

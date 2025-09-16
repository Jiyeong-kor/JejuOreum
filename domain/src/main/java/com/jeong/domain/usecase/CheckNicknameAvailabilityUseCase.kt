package com.jeong.domain.usecase

import com.jeong.domain.repository.UserProfileRepository

class CheckNicknameAvailabilityUseCase(
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(nickname: String): Result<Boolean> =
        userProfileRepository.isNicknameAvailable(nickname)
}

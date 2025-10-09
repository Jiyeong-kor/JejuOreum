package com.jeong.jejuoreum.domain.usecase

import com.jeong.jejuoreum.domain.repository.UserProfileRepository

class CheckNicknameAvailabilityUseCase(
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(nickname: String): Result<Boolean> =
        userProfileRepository.isNicknameAvailable(nickname)
}

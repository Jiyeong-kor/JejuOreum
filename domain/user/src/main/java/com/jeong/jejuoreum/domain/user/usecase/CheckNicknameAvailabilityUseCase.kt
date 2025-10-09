package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.repository.UserProfileRepository
import javax.inject.Inject

class CheckNicknameAvailabilityUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(nickname: String): Result<Boolean> =
        userProfileRepository.isNicknameAvailable(nickname)
}

package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.repository.UserProfileRepository
import javax.inject.Inject

class IsUserRegisteredUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(): Result<Boolean> =
        userProfileRepository.isUserRegistered()
}

package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.repository.UserInteractionRepository
import javax.inject.Inject

class GetUserStampStatusesUseCase @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository,
) {
    suspend operator fun invoke(): Map<String, Boolean> =
        userInteractionRepository.getAllStampStatus()
}

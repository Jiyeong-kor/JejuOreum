package com.jeong.domain.usecase.oreum

import com.jeong.domain.repository.UserInteractionRepository
import javax.inject.Inject

class GetUserStampStatusesUseCase @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository
) {
    suspend operator fun invoke(): Map<String, Boolean> =
        userInteractionRepository.getAllStampStatus()
}

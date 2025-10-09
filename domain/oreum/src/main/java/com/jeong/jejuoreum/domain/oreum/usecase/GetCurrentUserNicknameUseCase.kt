package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.user.repository.UserInteractionRepository
import javax.inject.Inject

class GetCurrentUserNicknameUseCase @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository,
) {

    suspend operator fun invoke(): Result<String> =
        runCatching { userInteractionRepository.getCurrentUserNickname() }
}

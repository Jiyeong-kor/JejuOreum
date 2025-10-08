package com.jeong.domain.usecase.oreum

import com.jeong.domain.repository.UserInteractionRepository
import javax.inject.Inject

class GetCurrentUserNicknameUseCase @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository,
) {

    suspend operator fun invoke(): Result<String> =
        runCatching { userInteractionRepository.getCurrentUserNickname() }
}

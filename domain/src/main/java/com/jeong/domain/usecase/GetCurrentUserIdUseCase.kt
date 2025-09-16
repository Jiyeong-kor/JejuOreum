package com.jeong.domain.usecase

import com.jeong.domain.repository.UserAuthRepository

class GetCurrentUserIdUseCase(
    private val userAuthRepository: UserAuthRepository,
) {
    operator fun invoke(): String? = userAuthRepository.currentUserId()
}

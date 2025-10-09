package com.jeong.jejuoreum.domain.usecase

import com.jeong.jejuoreum.domain.repository.UserAuthRepository

class GetCurrentUserIdUseCase(
    private val userAuthRepository: UserAuthRepository,
) {
    operator fun invoke(): String? = userAuthRepository.currentUserId()
}

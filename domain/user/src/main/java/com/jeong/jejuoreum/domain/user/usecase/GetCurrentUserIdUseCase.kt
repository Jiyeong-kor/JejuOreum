package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.repository.UserAuthRepository

class GetCurrentUserIdUseCase(
    private val userAuthRepository: UserAuthRepository,
) {
    operator fun invoke(): String? = userAuthRepository.currentUserId()
}

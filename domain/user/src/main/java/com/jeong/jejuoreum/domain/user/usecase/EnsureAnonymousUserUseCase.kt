package com.jeong.jejuoreum.domain.usecase

import com.jeong.jejuoreum.domain.entity.UserAccount
import com.jeong.jejuoreum.domain.repository.UserAuthRepository

class EnsureAnonymousUserUseCase(
    private val userAuthRepository: UserAuthRepository,
) {
    suspend operator fun invoke(): Result<UserAccount> =
        userAuthRepository.ensureAnonymousUser()
}

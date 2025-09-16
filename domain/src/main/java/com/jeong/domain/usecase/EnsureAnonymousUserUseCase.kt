package com.jeong.domain.usecase

import com.jeong.domain.entity.UserAccount
import com.jeong.domain.repository.UserAuthRepository

class EnsureAnonymousUserUseCase(
    private val userAuthRepository: UserAuthRepository,
) {
    suspend operator fun invoke(): Result<UserAccount> =
        userAuthRepository.ensureAnonymousUser()
}

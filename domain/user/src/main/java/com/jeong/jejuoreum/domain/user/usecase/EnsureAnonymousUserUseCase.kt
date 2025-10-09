package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.entity.UserAccount
import com.jeong.jejuoreum.domain.user.repository.UserAuthRepository

class EnsureAnonymousUserUseCase(
    private val userAuthRepository: UserAuthRepository,
) {
    suspend operator fun invoke(): Result<UserAccount> =
        userAuthRepository.ensureAnonymousUser()
}

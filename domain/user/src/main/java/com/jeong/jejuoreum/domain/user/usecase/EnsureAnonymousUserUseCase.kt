package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.entity.UserAccount
import com.jeong.jejuoreum.domain.user.repository.UserAuthRepository
import javax.inject.Inject

class EnsureAnonymousUserUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
) {
    suspend operator fun invoke(): Result<UserAccount> =
        userAuthRepository.ensureAnonymousUser()
}

package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.repository.UserAuthRepository
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
) {
    operator fun invoke(): String? = userAuthRepository.currentUserId()
}

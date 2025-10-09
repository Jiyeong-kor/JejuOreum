package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.entity.UserAccount
import com.jeong.jejuoreum.domain.user.repository.UserProfileRepository

class SaveNicknameUseCase(
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(nickname: String): Result<UserAccount> =
        userProfileRepository.saveNickname(nickname)
}

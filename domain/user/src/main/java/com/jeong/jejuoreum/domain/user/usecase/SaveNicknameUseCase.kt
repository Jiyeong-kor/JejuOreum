package com.jeong.domain.usecase

import com.jeong.domain.entity.UserAccount
import com.jeong.domain.repository.UserProfileRepository

class SaveNicknameUseCase(
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(nickname: String): Result<UserAccount> =
        userProfileRepository.saveNickname(nickname)
}

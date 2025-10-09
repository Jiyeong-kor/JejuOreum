package com.jeong.jejuoreum.domain.usecase

import com.jeong.jejuoreum.domain.entity.UserAccount
import com.jeong.jejuoreum.domain.repository.UserProfileRepository

class SaveNicknameUseCase(
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(nickname: String): Result<UserAccount> =
        userProfileRepository.saveNickname(nickname)
}

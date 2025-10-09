package com.jeong.jejuoreum.domain.repository

import com.jeong.jejuoreum.domain.entity.UserAccount

interface UserProfileRepository {
    suspend fun isNicknameAvailable(nickname: String): Result<Boolean>
    suspend fun saveNickname(nickname: String): Result<UserAccount>
}

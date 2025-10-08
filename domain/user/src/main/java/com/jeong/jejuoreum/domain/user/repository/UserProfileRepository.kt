package com.jeong.domain.repository

import com.jeong.domain.entity.UserAccount

interface UserProfileRepository {
    suspend fun isNicknameAvailable(nickname: String): Result<Boolean>
    suspend fun saveNickname(nickname: String): Result<UserAccount>
}

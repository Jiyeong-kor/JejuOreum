package com.jeong.jejuoreum.domain.user.repository

import com.jeong.jejuoreum.domain.user.entity.UserAccount

interface UserProfileRepository {
    suspend fun isNicknameAvailable(nickname: String): Result<Boolean>
    suspend fun saveNickname(nickname: String): Result<UserAccount>
    suspend fun isUserRegistered(): Result<Boolean>
}

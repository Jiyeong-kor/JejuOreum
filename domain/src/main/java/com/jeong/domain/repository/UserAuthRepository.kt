package com.jeong.domain.repository

import com.jeong.domain.entity.UserAccount

interface UserAuthRepository {
    suspend fun ensureAnonymousUser(): Result<UserAccount>
    fun currentUserId(): String?
}

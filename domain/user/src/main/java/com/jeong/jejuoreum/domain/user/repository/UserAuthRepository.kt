package com.jeong.jejuoreum.domain.repository

import com.jeong.jejuoreum.domain.entity.UserAccount

interface UserAuthRepository {
    suspend fun ensureAnonymousUser(): Result<UserAccount>
    fun currentUserId(): String?
}

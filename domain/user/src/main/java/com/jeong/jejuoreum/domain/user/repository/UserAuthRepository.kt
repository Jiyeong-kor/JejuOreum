package com.jeong.jejuoreum.domain.user.repository

import com.jeong.jejuoreum.domain.user.entity.UserAccount

interface UserAuthRepository {
    suspend fun ensureAnonymousUser(): Result<UserAccount>
    fun currentUserId(): String?
}

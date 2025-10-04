package com.jeong.feature.join.domain

import com.jeong.domain.entity.UserAccount

interface AnonymousUserInitializer {
    suspend fun ensure(): Result<UserAccount>
}

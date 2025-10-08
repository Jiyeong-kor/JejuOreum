package com.jeong.jejuoreum.feature.onboarding.domain

import com.jeong.jejuoreum.domain.user.entity.UserAccount

interface NicknameSaver {
    suspend fun save(nickname: String): Result<UserAccount>
}

package com.jeong.jejuoreum.feature.onboarding.domain

import com.jeong.jejuoreum.domain.user.entity.UserAccount

interface AnonymousUserInitializer {
    suspend fun ensure(): Result<UserAccount>
}

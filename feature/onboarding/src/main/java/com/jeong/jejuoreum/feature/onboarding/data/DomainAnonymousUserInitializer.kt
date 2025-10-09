package com.jeong.jejuoreum.feature.onboarding.data

import com.jeong.jejuoreum.domain.user.usecase.EnsureAnonymousUserUseCase
import com.jeong.jejuoreum.feature.onboarding.domain.AnonymousUserInitializer
import javax.inject.Inject

class DomainAnonymousUserInitializer @Inject constructor(
    private val ensureAnonymousUserUseCase: EnsureAnonymousUserUseCase,
) : AnonymousUserInitializer {
    override suspend fun ensure() = ensureAnonymousUserUseCase()
}

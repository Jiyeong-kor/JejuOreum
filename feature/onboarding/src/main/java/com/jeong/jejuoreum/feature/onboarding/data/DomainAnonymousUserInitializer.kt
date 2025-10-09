package com.jeong.jejuoreum.feature.join.data

import com.jeong.jejuoreum.domain.user.usecase.EnsureAnonymousUserUseCase
import com.jeong.jejuoreum.feature.join.domain.AnonymousUserInitializer
import javax.inject.Inject

class DomainAnonymousUserInitializer @Inject constructor(
    private val ensureAnonymousUserUseCase: EnsureAnonymousUserUseCase,
) : AnonymousUserInitializer {
    override suspend fun ensure() = ensureAnonymousUserUseCase()
}

package com.jeong.feature.join.data

import com.jeong.domain.usecase.EnsureAnonymousUserUseCase
import com.jeong.feature.join.domain.AnonymousUserInitializer
import javax.inject.Inject

class DomainAnonymousUserInitializer @Inject constructor(
    private val ensureAnonymousUserUseCase: EnsureAnonymousUserUseCase,
) : AnonymousUserInitializer {
    override suspend fun ensure() = ensureAnonymousUserUseCase()
}

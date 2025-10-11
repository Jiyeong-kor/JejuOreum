package com.jeong.jejuoreum.domain.oreum.usecase

import javax.inject.Inject

class PreloadOreumSummariesUseCase @Inject constructor(
    private val refreshOreumsUseCase: RefreshOreumsUseCase,
) {
    suspend operator fun invoke(): Result<Unit> {
        return refreshOreumsUseCase()
    }
}

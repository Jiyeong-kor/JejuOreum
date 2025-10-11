package com.jeong.jejuoreum.domain.oreum.usecase

import javax.inject.Inject

class LoadOreumSummariesUseCase @Inject constructor(
    private val refreshOreumsUseCase: RefreshOreumsUseCase
) {
    suspend operator fun invoke(): Result<Unit> = refreshOreumsUseCase()
}

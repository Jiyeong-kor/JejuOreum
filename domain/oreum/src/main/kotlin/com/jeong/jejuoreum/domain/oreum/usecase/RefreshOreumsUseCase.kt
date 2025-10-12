package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.core.common.result.JejuOreumResult
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository

class RefreshOreumsUseCase(
    private val repository: OreumRepository
) {
    suspend operator fun invoke(): JejuOreumResult<Unit> = repository.refreshOreums()
}

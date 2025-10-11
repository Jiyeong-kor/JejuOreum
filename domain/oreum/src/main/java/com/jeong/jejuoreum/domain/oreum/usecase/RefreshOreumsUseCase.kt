package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject

class RefreshOreumsUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        runCatching { oreumRepository.syncOreums() }
}

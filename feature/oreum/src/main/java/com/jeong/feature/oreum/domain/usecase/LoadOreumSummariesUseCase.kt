package com.jeong.feature.oreum.domain.usecase

import com.jeong.domain.repository.OreumRepository
import javax.inject.Inject

class LoadOreumSummariesUseCase @Inject constructor(
    private val oreumRepository: OreumRepository
) {
    suspend operator fun invoke(): Result<Unit> = oreumRepository.loadOreumListIfNeeded()
}

package com.jeong.jejuoreum.domain.usecase.oreum

import com.jeong.jejuoreum.domain.repository.OreumRepository
import javax.inject.Inject

class PreloadOreumSummariesUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return oreumRepository.loadOreumListIfNeeded()
    }
}

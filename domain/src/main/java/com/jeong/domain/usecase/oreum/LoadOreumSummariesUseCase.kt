package com.jeong.domain.usecase.oreum

import com.jeong.domain.repository.OreumRepository
import javax.inject.Inject

class LoadOreumSummariesUseCase @Inject constructor(
    private val oreumRepository: OreumRepository
) {
    suspend operator fun invoke(): Result<Unit> = oreumRepository.loadOreumListIfNeeded()
}

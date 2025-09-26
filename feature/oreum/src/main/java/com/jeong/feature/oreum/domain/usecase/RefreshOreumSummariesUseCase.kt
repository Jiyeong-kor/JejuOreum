package com.jeong.feature.oreum.domain.usecase

import com.jeong.domain.repository.OreumRepository
import javax.inject.Inject

class RefreshOreumSummariesUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {

    suspend operator fun invoke(): Result<Unit> =
        runCatching { oreumRepository.refreshAllOreumsWithNewUserData() }
}

package com.jeong.feature.oreum.domain.usecase

import com.jeong.domain.repository.UserInteractionRepository
import javax.inject.Inject

class GetOreumStampStatusUseCase @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository,
) {

    suspend operator fun invoke(oreumIdx: String): Result<Boolean> =
        runCatching { userInteractionRepository.getStampStatus(oreumIdx) }
}

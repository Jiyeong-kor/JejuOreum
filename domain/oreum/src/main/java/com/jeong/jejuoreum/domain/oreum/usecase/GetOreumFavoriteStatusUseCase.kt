package com.jeong.jejuoreum.domain.usecase.oreum

import com.jeong.jejuoreum.domain.repository.UserInteractionRepository
import javax.inject.Inject

class GetOreumFavoriteStatusUseCase @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository,
) {

    suspend operator fun invoke(oreumIdx: String): Result<Boolean> =
        runCatching { userInteractionRepository.getFavoriteStatus(oreumIdx) }
}

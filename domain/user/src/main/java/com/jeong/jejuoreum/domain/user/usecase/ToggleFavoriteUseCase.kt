package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.repository.UserInteractionRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository,
) {
    suspend operator fun invoke(oreumIdx: String, newIsFavorite: Boolean): Result<Int> =
        runCatching { userInteractionRepository.toggleFavorite(oreumIdx, newIsFavorite) }
}

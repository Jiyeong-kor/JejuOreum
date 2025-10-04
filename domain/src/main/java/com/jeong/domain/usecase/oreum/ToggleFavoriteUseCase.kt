package com.jeong.domain.usecase

import com.jeong.domain.repository.OreumRepository
import com.jeong.domain.repository.UserInteractionRepository

class ToggleFavoriteUseCase(
    private val userInteractionRepository: UserInteractionRepository,
    private val oreumRepository: OreumRepository
) {
    suspend operator fun invoke(oreumIdx: String, newIsFavorite: Boolean): Int {
        val newTotal = userInteractionRepository.toggleFavorite(oreumIdx, newIsFavorite)
        oreumRepository.refreshAllOreumsWithNewUserData()
        return newTotal
    }
}

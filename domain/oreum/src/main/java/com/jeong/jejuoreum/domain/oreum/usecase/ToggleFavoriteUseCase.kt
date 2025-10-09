package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import com.jeong.jejuoreum.domain.user.repository.UserInteractionRepository

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

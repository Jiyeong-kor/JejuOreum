package com.jeong.jejuoreum.domain.usecase.oreum

import com.jeong.jejuoreum.domain.repository.OreumRepository
import com.jeong.jejuoreum.domain.repository.UserInteractionRepository

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

package com.jeong.jjoreum.domain.usecase

import com.jeong.jjoreum.repository.OreumRepository
import com.jeong.jjoreum.repository.UserInteractionRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository,
    private val oreumRepository: OreumRepository
) {
    suspend operator fun invoke(oreumIdx: String, newIsFavorite: Boolean): Int {
        val newTotal = userInteractionRepository.toggleFavorite(oreumIdx, newIsFavorite)
        oreumRepository.refreshAllOreumsWithNewUserData()
        return newTotal
    }
}

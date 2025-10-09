package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.repository.StampRepository
import javax.inject.Inject

class TryStampUseCase @Inject constructor(
    private val stampRepository: StampRepository
) {
    suspend operator fun invoke(
        oreumIdx: String,
        oreumName: String,
        oreumLat: Double,
        oreumLng: Double,
    ): Result<Unit> = stampRepository.tryStamp(oreumIdx, oreumName, oreumLat, oreumLng)
}

package com.jeong.domain.usecase.oreum

import com.jeong.domain.repository.StampRepository
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

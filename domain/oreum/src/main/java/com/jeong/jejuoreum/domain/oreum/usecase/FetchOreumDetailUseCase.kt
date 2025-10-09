package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject

class FetchOreumDetailUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {

    suspend operator fun invoke(oreumIdx: String): Result<ResultSummary> =
        runCatching { oreumRepository.fetchSingleOreumById(oreumIdx) }
}

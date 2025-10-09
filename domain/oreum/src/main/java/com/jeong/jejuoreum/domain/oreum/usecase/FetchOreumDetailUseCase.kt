package com.jeong.jejuoreum.domain.usecase.oreum

import com.jeong.jejuoreum.domain.entity.ResultSummary
import com.jeong.jejuoreum.domain.repository.OreumRepository
import javax.inject.Inject

class FetchOreumDetailUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {

    suspend operator fun invoke(oreumIdx: String): Result<ResultSummary> =
        runCatching { oreumRepository.fetchSingleOreumById(oreumIdx) }
}

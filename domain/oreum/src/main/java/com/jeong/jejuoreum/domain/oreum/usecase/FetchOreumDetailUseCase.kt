package com.jeong.domain.usecase.oreum

import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.repository.OreumRepository
import javax.inject.Inject

class FetchOreumDetailUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {

    suspend operator fun invoke(oreumIdx: String): Result<ResultSummary> =
        runCatching { oreumRepository.fetchSingleOreumById(oreumIdx) }
}

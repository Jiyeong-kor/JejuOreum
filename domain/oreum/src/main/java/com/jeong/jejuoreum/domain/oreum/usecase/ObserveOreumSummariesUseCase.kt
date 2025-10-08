package com.jeong.domain.usecase.oreum

import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.repository.OreumRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveOreumSummariesUseCase @Inject constructor(
    private val oreumRepository: OreumRepository
) {
    operator fun invoke(): StateFlow<List<ResultSummary>> = oreumRepository.oreumListFlow
}

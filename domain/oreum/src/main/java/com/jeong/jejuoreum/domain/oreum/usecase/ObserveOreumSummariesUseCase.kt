package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveOreumSummariesUseCase @Inject constructor(
    private val oreumRepository: OreumRepository
) {
    operator fun invoke(): StateFlow<List<ResultSummary>> = oreumRepository.oreumListFlow
}

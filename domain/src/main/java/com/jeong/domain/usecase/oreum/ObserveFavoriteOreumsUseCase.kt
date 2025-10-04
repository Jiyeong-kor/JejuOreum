package com.jeong.feature.oreum.domain.usecase

import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.repository.OreumRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveFavoriteOreumsUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {

    operator fun invoke(): Flow<List<ResultSummary>> =
        oreumRepository.oreumListFlow.map { oreums ->
            oreums.filter(ResultSummary::userLiked)
        }
}

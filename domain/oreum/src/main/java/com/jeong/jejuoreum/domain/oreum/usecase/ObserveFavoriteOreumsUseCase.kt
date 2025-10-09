package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
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

package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.core.common.error.toResourceError
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetOreumSummariesUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {
    operator fun invoke(): Flow<Resource<List<ResultSummary>>> =
        oreumRepository.observeOreumSummaries()
            .map<List<ResultSummary>, Resource<List<ResultSummary>>> { summaries ->
                Resource.Success(summaries)
            }
            .onStart {
                emit(Resource.Loading)
                runCatching { oreumRepository.syncOreums() }
                    .onFailure { throwable -> emit(Resource.Error(throwable.toResourceError())) }
            }
            .catch { throwable -> emit(Resource.Error(throwable.toResourceError())) }
}

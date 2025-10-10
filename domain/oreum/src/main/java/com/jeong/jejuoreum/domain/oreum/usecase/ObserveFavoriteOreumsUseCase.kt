package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.common.result.ResultResource
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveFavoriteOreumsUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {

    operator fun invoke(): Flow<ResultResource<List<ResultSummary>>> =
        oreumRepository.observeOreumSummaries().map { result ->
            result.fold(
                onSuccess = { resource ->
                    val mapped = when (resource) {
                        Resource.Loading -> Resource.Loading
                        is Resource.Error -> Resource.Error(resource.throwable)
                        is Resource.Success -> Resource.Success(
                            resource.data.filter(ResultSummary::userLiked)
                        )
                    }
                    Result.success(mapped)
                },
                onFailure = { throwable -> Result.failure(throwable) }
            )
        }
}

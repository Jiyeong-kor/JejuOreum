package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveFavoriteOreumsUseCase @Inject constructor(
    private val getOreumSummariesUseCase: GetOreumSummariesUseCase,
) {

    operator fun invoke(): Flow<Resource<List<ResultSummary>>> =
        getOreumSummariesUseCase().map { resource ->
            when (resource) {
                Resource.Loading -> Resource.Loading
                is Resource.Error -> resource
                is Resource.Success -> Resource.Success(
                    resource.data.filter(ResultSummary::userLiked)
                )
            }
        }
}

package com.jeong.jejuoreum.feature.map.domain

import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.feature.map.domain.mapper.OreumOverviewMapper
import com.jeong.jejuoreum.feature.map.domain.model.OreumOverview
import com.jeong.jejuoreum.domain.oreum.usecase.GetOreumDetailUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveOreumsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OreumOverviewInteractor @Inject constructor(
    private val observeOreumsUseCase: ObserveOreumsUseCase,
    private val getOreumDetailUseCase: GetOreumDetailUseCase,
    private val mapper: OreumOverviewMapper
) {

    fun observeOreumOverviews(): Flow<Resource<List<OreumOverview>>> =
        observeOreumsUseCase(Unit).map { resource ->
            when (resource) {
                Resource.Loading -> Resource.Loading
                is Resource.Error -> resource
                is Resource.Success -> Resource.Success(
                    resource.data.map(mapper::map)
                )
            }
        }

    suspend fun getOreumOverview(oreumId: String): Result<OreumOverview> =
        getOreumDetailUseCase(oreumId).map(mapper::map)
}

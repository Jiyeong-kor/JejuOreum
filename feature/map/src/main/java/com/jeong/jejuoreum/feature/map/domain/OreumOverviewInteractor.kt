package com.jeong.jejuoreum.feature.oreum.domain

import com.jeong.jejuoreum.feature.oreum.domain.mapper.OreumOverviewMapper
import com.jeong.jejuoreum.feature.oreum.domain.model.OreumOverview
import com.jeong.jejuoreum.domain.usecase.GetOreumDetailUseCase
import com.jeong.jejuoreum.domain.usecase.ObserveOreumsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface OreumOverviewInteractor {
    fun observeOreumOverviews(): Flow<Result<List<OreumOverview>>>
    suspend fun getOreumOverview(oreumId: String): Result<OreumOverview>
}

class DefaultOreumOverviewInteractor @Inject constructor(
    private val observeOreumsUseCase: ObserveOreumsUseCase,
    private val getOreumDetailUseCase: GetOreumDetailUseCase,
    private val mapper: OreumOverviewMapper
) : OreumOverviewInteractor {

    override fun observeOreumOverviews(): Flow<Result<List<OreumOverview>>> =
        observeOreumsUseCase(Unit).map { result ->
            result.map { oreums -> oreums.map(mapper::map) }
        }

    override suspend fun getOreumOverview(oreumId: String): Result<OreumOverview> =
        getOreumDetailUseCase(oreumId).map(mapper::map)
}

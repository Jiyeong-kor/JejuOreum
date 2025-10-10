package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.core.common.result.ResultResource
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import com.jeong.jejuoreum.domain.oreum.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveOreumsUseCase @Inject constructor(
    private val repository: OreumRepository,
) : FlowUseCase<Unit, Flow<ResultResource<List<Oreum>>>> {

    override suspend fun invoke(params: Unit): Flow<ResultResource<List<Oreum>>> =
        repository.observeOreums()
}

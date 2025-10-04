package com.jeong.domain.usecase

import com.jeong.domain.model.Oreum
import com.jeong.domain.repository.OreumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveOreumsUseCase @Inject constructor(
    private val repository: OreumRepository,
) : FlowUseCase<Unit, Result<List<Oreum>>> {

    override fun invoke(params: Unit): Flow<Result<List<Oreum>>> = repository.observeOreums()
}

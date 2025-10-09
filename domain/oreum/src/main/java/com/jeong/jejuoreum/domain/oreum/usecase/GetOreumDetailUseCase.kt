package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.core.common.usecase.SuspendUseCase
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject

class GetOreumDetailUseCase @Inject constructor(
    private val repository: OreumRepository,
) : SuspendUseCase<String, Result<Oreum>> {

    override suspend fun invoke(params: String): Result<Oreum> = repository.getOreumDetail(params)
}

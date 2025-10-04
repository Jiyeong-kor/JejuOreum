package com.jeong.domain.usecase

import com.jeong.domain.model.Oreum
import com.jeong.domain.repository.OreumRepository
import javax.inject.Inject

class GetOreumDetailUseCase @Inject constructor(
    private val repository: OreumRepository,
) : SuspendUseCase<String, Result<Oreum>> {

    override suspend fun invoke(params: String): Result<Oreum> = repository.getOreumDetail(params)
}

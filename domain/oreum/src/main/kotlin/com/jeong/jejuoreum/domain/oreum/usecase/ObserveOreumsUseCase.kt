package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import kotlinx.coroutines.flow.Flow

class ObserveOreumsUseCase(
    private val repository: OreumRepository
) {
    operator fun invoke(): Flow<List<com.jeong.jejuoreum.domain.oreum.model.Oreum>> = repository.observeOreums()
}

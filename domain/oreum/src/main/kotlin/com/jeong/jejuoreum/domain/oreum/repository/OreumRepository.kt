package com.jeong.jejuoreum.domain.oreum.repository

import com.jeong.jejuoreum.core.common.result.JejuOreumResult
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import kotlinx.coroutines.flow.Flow

interface OreumRepository {
    fun observeOreums(): Flow<List<Oreum>>
    suspend fun refreshOreums(): JejuOreumResult<Unit>
}

package com.jeong.jejuoreum.domain.oreum.repository

import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import kotlinx.coroutines.flow.Flow

interface OreumRepository {
    fun observeOreums(): Flow<List<Oreum>>
    fun observeOreumSummaries(): Flow<List<ResultSummary>>
    suspend fun getOreumDetail(id: String): Result<Oreum>
    suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary
    suspend fun syncOreums()

    companion object {
        fun notFound(id: String): Result<Nothing> = Result.failure(DomainError.NotFound(id))
    }
}

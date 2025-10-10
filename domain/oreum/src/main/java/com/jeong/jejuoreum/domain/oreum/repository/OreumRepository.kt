package com.jeong.jejuoreum.domain.oreum.repository

import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.core.common.result.ResultResource
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface OreumRepository {
    fun observeOreums(): Flow<ResultResource<List<Oreum>>>
    fun observeOreumSummaries(): Flow<ResultResource<List<ResultSummary>>>
    suspend fun getOreumDetail(id: String): Result<Oreum>
    val oreumListFlow: StateFlow<List<ResultSummary>>

    suspend fun loadOreumListIfNeeded(): Result<Unit>
    suspend fun refreshAllOreumsWithNewUserData()
    fun getCachedOreumList(): List<ResultSummary>
    suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary

    companion object {
        fun notFound(id: String): Result<Nothing> = Result.failure(DomainError.NotFound(id))
    }
}

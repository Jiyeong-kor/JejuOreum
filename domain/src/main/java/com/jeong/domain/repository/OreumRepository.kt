package com.jeong.domain.repository

import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.error.DomainError
import com.jeong.domain.model.Oreum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface OreumRepository {
    fun observeOreums(): Flow<Result<List<Oreum>>>
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

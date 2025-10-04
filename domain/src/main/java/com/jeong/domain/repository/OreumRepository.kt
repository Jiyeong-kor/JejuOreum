package com.jeong.domain.repository

import com.jeong.domain.error.DomainError
import com.jeong.domain.model.Oreum
import kotlinx.coroutines.flow.Flow

interface OreumRepository {
    fun observeOreums(): Flow<Result<List<Oreum>>>
    suspend fun getOreumDetail(id: String): Result<Oreum>

    companion object {
        fun notFound(id: String): Result<Nothing> = Result.failure(DomainError.NotFound(id))
    }
}

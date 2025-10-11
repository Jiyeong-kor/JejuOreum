package com.jeong.jejuoreum.core.testing.fake

import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeOreumRepository : OreumRepository {

    private val oreumFlow = MutableStateFlow<List<Oreum>>(emptyList())
    private val summaryFlow = MutableStateFlow<List<ResultSummary>>(emptyList())

    var syncResult: Result<Unit> = Result.success(Unit)

    override fun observeOreums(): Flow<List<Oreum>> = oreumFlow.asStateFlow()

    override fun observeOreumSummaries(): Flow<List<ResultSummary>> = summaryFlow.asStateFlow()

    override suspend fun getOreumDetail(id: String): Result<Oreum> {
        val oreum = oreumFlow.value.firstOrNull { it.id == id }
        return oreum?.let { Result.success(it) } ?: OreumRepository.notFound(id)
    }

    override suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary =
        summaryFlow.value.firstOrNull { it.idx.toString() == oreumIdx }
            ?: throw DomainError.NotFound(oreumIdx)

    override suspend fun syncOreums() {
        syncResult.getOrThrow()
    }

    fun emitOreums(oreums: List<Oreum>) {
        oreumFlow.value = oreums
    }

    fun emitSummaries(summaries: List<ResultSummary>) {
        summaryFlow.value = summaries
    }
}

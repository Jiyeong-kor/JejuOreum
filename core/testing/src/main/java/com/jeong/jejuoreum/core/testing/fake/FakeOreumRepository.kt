package com.jeong.jejuoreum.core.testing.fake

import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeOreumRepository : OreumRepository {

    private val oreumResults = MutableStateFlow<Resource<List<Oreum>>>(Resource.Loading)
    private val summaryResults = MutableStateFlow<Resource<List<ResultSummary>>>(Resource.Loading)
    private val summariesState = MutableStateFlow(emptyList<ResultSummary>())

    var loadResult: Result<Unit> = Result.success(Unit)

    override fun observeOreums(): Flow<Resource<List<Oreum>>> = oreumResults

    override fun observeOreumSummaries(): Flow<Resource<List<ResultSummary>>> = summaryResults

    override suspend fun getOreumDetail(id: String): Result<Oreum> {
        val oreum = (oreumResults.value as? Resource.Success)?.data?.firstOrNull { it.id == id }
        return oreum?.let { Result.success(it) } ?: OreumRepository.notFound(id)
    }

    override suspend fun loadOreumListIfNeeded(): Result<Unit> = loadResult

    override suspend fun refreshAllOreumsWithNewUserData() {
        // No-op for tests
    }

    override fun getCachedOreumList(): List<ResultSummary> = summariesState.value

    override suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary =
        summariesState.value.firstOrNull { it.idx.toString() == oreumIdx }
            ?: throw DomainError.NotFound(oreumIdx)

    fun emitOreums(resource: Resource<List<Oreum>>) {
        oreumResults.value = resource
    }

    fun emitSummaries(resource: Resource<List<ResultSummary>>) {
        summaryResults.value = resource
        if (resource is Resource.Success) {
            summariesState.value = resource.data
        }
    }
}

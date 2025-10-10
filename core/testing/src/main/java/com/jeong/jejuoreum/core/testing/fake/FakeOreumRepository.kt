package com.jeong.jejuoreum.core.testing.fake

import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.common.result.ResultResource
import com.jeong.jejuoreum.core.common.result.dataOrNull
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeOreumRepository : OreumRepository {

    private val oreumResults = MutableStateFlow<ResultResource<List<Oreum>>>(Result.success(Resource.Loading))
    private val summaryResults = MutableStateFlow<ResultResource<List<ResultSummary>>>(Result.success(Resource.Loading))
    private val summariesState = MutableStateFlow(emptyList<ResultSummary>())

    var loadResult: Result<Unit> = Result.success(Unit)

    override val oreumListFlow: StateFlow<List<ResultSummary>> = summariesState.asStateFlow()

    override fun observeOreums(): Flow<ResultResource<List<Oreum>>> = oreumResults

    override fun observeOreumSummaries(): Flow<ResultResource<List<ResultSummary>>> = summaryResults

    override suspend fun getOreumDetail(id: String): Result<Oreum> {
        val oreum = oreumResults.value.dataOrNull()?.firstOrNull { it.id == id }
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

    fun emitOreums(resource: ResultResource<List<Oreum>>) {
        oreumResults.value = resource
    }

    fun emitSummaries(resource: ResultResource<List<ResultSummary>>) {
        summaryResults.value = resource
        resource.getOrNull()?.let { res ->
            if (res is Resource.Success) {
                summariesState.value = res.data
            }
        }
    }
}

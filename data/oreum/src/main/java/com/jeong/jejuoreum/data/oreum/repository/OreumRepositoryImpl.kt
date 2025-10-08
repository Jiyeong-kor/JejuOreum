package com.jeong.jejuoreum.data.oreum.repository

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.data.oreum.datasource.local.OreumLocalDataSource
import com.jeong.jejuoreum.data.oreum.mapper.toDomainOreum
import com.jeong.jejuoreum.data.oreum.mapper.toDomainSummary
import com.jeong.jejuoreum.data.oreum.mapper.toEntity
import com.jeong.jejuoreum.data.remote.oreum.datasource.OreumRemoteDataSource
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Singleton
class OreumRepositoryImpl @Inject constructor(
    private val remoteDataSource: OreumRemoteDataSource,
    private val localDataSource: OreumLocalDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : OreumRepository {

    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    override val oreumListFlow: StateFlow<List<ResultSummary>> =
        localDataSource.observeOreums()
            .map { entities -> entities.map { it.toDomainSummary() } }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    init {
        scope.launch { loadOreumListIfNeeded() }
    }

    override fun observeOreums(): Flow<Result<List<Oreum>>> = flow {
        loadOreumListIfNeeded().onFailure { emit(Result.failure(it)) }
        emitAll(
            oreumListFlow.map { summaries ->
                Result.success(summaries.map { it.toDomainOreum() })
            }
        )
    }

    override suspend fun getOreumDetail(id: String): Result<Oreum> =
        runCatching { fetchSingleOreumById(id).toDomainOreum() }

    override suspend fun loadOreumListIfNeeded(): Result<Unit> =
        withContext(dispatcherProvider.io) {
            runCatching {
                if (oreumListFlow.value.isEmpty()) {
                    refreshFromRemote()
                }
            }
        }

    override suspend fun refreshAllOreumsWithNewUserData() {
        withContext(dispatcherProvider.io) { refreshFromRemote() }
    }

    override fun getCachedOreumList(): List<ResultSummary> = oreumListFlow.value

    override suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary =
        withContext(dispatcherProvider.io) {
            oreumListFlow.value.firstOrNull { it.idx.toString() == oreumIdx }
                ?: remoteDataSource.fetchOreum(oreumIdx)?.also { summary ->
                    localDataSource.upsert(summary.toEntity())
                }
                ?: throw DomainError.NotFound(oreumIdx)
        }

    private suspend fun refreshFromRemote() {
        val remote = remoteDataSource.fetchOreums()
        val merged = mergeUserState(remote, oreumListFlow.value)
        localDataSource.upsertAll(merged.map { it.toEntity() })
    }

    private fun mergeUserState(
        remote: List<ResultSummary>,
        local: List<ResultSummary>
    ): List<ResultSummary> {
        if (local.isEmpty()) return remote
        val localByIdx = local.associateBy { it.idx }
        return remote.map { summary ->
            val cached = localByIdx[summary.idx]
            if (cached != null) {
                summary.copy(
                    userLiked = cached.userLiked,
                    userStamped = cached.userStamped,
                    totalFavorites = if (summary.totalFavorites == 0) cached.totalFavorites else summary.totalFavorites,
                    totalStamps = if (summary.totalStamps == 0) cached.totalStamps else summary.totalStamps,
                )
            } else {
                summary
            }
        }
    }
}

package com.jeong.jejuoreum.data.oreum.repository

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.core.common.error.toResourceError
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.data.oreum.datasource.local.OreumLocalDataSource
import com.jeong.jejuoreum.data.oreum.datasource.remote.OreumRemoteDataSource
import com.jeong.jejuoreum.data.oreum.mapper.toDomainOreum
import com.jeong.jejuoreum.data.oreum.mapper.toDomainSummary
import com.jeong.jejuoreum.data.oreum.mapper.toEntity
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
internal class OreumRepositoryImpl @Inject constructor(
    private val remoteDataSource: OreumRemoteDataSource,
    private val localDataSource: OreumLocalDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : OreumRepository {

    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)
    private val refreshMutex = Mutex()

    private val oreumCache = MutableStateFlow(emptyList<ResultSummary>())
    private val oreumResourceState = MutableStateFlow<Resource<List<ResultSummary>>>(Resource.Loading)

    init {
        scope.launch { startLocalObservation() }
        scope.launch { loadOreumListIfNeeded() }
    }

    override fun observeOreums(): Flow<Resource<List<Oreum>>> =
        observeOreumSummaries().map { resource ->
            when (resource) {
                Resource.Loading -> Resource.Loading
                is Resource.Error -> resource
                is Resource.Success -> Resource.Success(resource.data.map { it.toDomainOreum() })
            }
        }

    override fun observeOreumSummaries(): Flow<Resource<List<ResultSummary>>> = oreumResourceState

    override suspend fun getOreumDetail(id: String): Result<Oreum> =
        runCatching { fetchSingleOreumById(id).toDomainOreum() }

    override suspend fun loadOreumListIfNeeded(): Result<Unit> =
        withContext(dispatcherProvider.io) {
            runCatching {
                if (oreumCache.value.isEmpty()) {
                    oreumResourceState.value = Resource.Loading
                    refreshFromRemote()
                }
            }.onFailure { throwable ->
                oreumResourceState.value = Resource.Error(throwable.toResourceError())
            }
        }

    override suspend fun refreshAllOreumsWithNewUserData() {
        withContext(dispatcherProvider.io) {
            runCatching {
                oreumResourceState.value = Resource.Loading
                refreshFromRemote()
            }.onFailure { throwable ->
                oreumResourceState.value = Resource.Error(throwable.toResourceError())
            }
        }
    }

    override fun getCachedOreumList(): List<ResultSummary> = oreumCache.value

    override suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary =
        withContext(dispatcherProvider.io) {
            oreumCache.value.firstOrNull { it.idx.toString() == oreumIdx }
                ?: remoteDataSource.fetchOreum(oreumIdx)?.also { summary ->
                    localDataSource.upsert(summary.toEntity())
                }
                ?: throw DomainError.NotFound(oreumIdx)
        }

    private suspend fun refreshFromRemote() = refreshMutex.withLock {
        val remote = remoteDataSource.fetchOreums()
        val merged = mergeUserState(remote, oreumCache.value)
        localDataSource.upsertAll(merged.map { it.toEntity() })
    }

    private suspend fun startLocalObservation() {
        localDataSource.observeOreums()
            .map { entities -> entities.map { it.toDomainSummary() } }
            .catch { throwable ->
                oreumResourceState.value = Resource.Error(throwable.toResourceError())
            }
            .collect { summaries ->
                oreumCache.value = summaries
                oreumResourceState.value = Resource.Success(summaries)
            }
    }

    private fun mergeUserState(
        remote: List<ResultSummary>,
        local: List<ResultSummary>,
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

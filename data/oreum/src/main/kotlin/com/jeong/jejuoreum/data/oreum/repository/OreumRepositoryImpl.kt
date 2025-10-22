package com.jeong.jejuoreum.data.oreum.repository

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.data.oreum.local.source.OreumLocalDataSource
import com.jeong.jejuoreum.data.oreum.remote.source.OreumRemoteDataSource
import com.jeong.jejuoreum.data.oreum.mapper.toDomainOreum
import com.jeong.jejuoreum.data.oreum.mapper.toDomainSummary
import com.jeong.jejuoreum.data.oreum.mapper.toEntity
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class OreumRepositoryImpl @Inject constructor(
    private val remoteDataSource: OreumRemoteDataSource,
    private val localDataSource: OreumLocalDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : OreumRepository {

    private val refreshMutex = Mutex()

    override fun observeOreums(): Flow<List<Oreum>> =
        localDataSource.observeOreums()
            .map { entities ->
                entities.map { entity ->
                    entity.toDomainSummary().toDomainOreum()
                }
            }
            .flowOn(dispatcherProvider.io)

    override fun observeOreumSummaries(): Flow<List<ResultSummary>> =
        localDataSource.observeOreums()
            .map { entities -> entities.map { it.toDomainSummary() } }
            .flowOn(dispatcherProvider.io)

    override suspend fun getOreumDetail(id: String): Result<Oreum> =
        runCatching { fetchSingleOreumById(id).toDomainOreum() }

    override suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary =
        withContext(dispatcherProvider.io) {
            localDataSource.findById(oreumIdx)?.toDomainSummary()
                ?: remoteDataSource.fetchOreum(oreumIdx)?.also { summary ->
                    localDataSource.upsert(summary.toEntity())
                }
                ?: throw DomainError.NotFound(oreumIdx)
        }

    override suspend fun syncOreums() {
        withContext(dispatcherProvider.io) {
            refreshMutex.withLock {
                val remoteSummaries = remoteDataSource.fetchOreums()
                val localSummaries = localDataSource.observeOreums()
                    .first()
                    .map { it.toDomainSummary() }
                val merged = mergeUserState(remoteSummaries, localSummaries)
                localDataSource.upsertAll(merged.map { it.toEntity() })
            }
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

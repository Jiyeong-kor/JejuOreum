package com.jeong.data.repository

import com.jeong.core.utils.coroutines.CoroutineDispatcherProvider
import com.jeong.data.datasource.local.OreumLocalDataSource
import com.jeong.data.datasource.remote.OreumRemoteDataSource
import com.jeong.data.mapper.toDomain
import com.jeong.data.mapper.toEntity
import com.jeong.domain.error.DomainError
import com.jeong.domain.model.Oreum
import com.jeong.domain.repository.OreumRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Singleton
class OreumRepositoryImpl @Inject constructor(
    private val remoteDataSource: OreumRemoteDataSource,
    private val localDataSource: OreumLocalDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : OreumRepository {

    override fun observeOreums(): Flow<Result<List<Oreum>>> =
        localDataSource.observeOreums()
            .map { oreums -> Result.success(oreums.map { it.toDomain() }) }
            .onStart {
                runCatching { refreshOreums() }
                    .onFailure { emit(Result.failure(it)) }
            }
            .catch { emit(Result.failure(it)) }

    override suspend fun getOreumDetail(id: String): Result<Oreum> = runCatching {
        withContext(dispatcherProvider.io) {
            val cached = localDataSource.findById(id)
            if (cached != null) {
                return@withContext cached.toDomain()
            }

            val remote = remoteDataSource.fetchOreum(id)
                ?: throw DomainError.NotFound(id)

            val entity = remote.toEntity()
            localDataSource.upsert(entity)
            entity.toDomain()
        }
    }

    private suspend fun refreshOreums() = withContext(dispatcherProvider.io) {
        val remoteOreums = remoteDataSource.fetchOreums()
        localDataSource.upsertAll(remoteOreums.map { it.toEntity() })
    }
}

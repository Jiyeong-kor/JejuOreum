package com.jeong.jejuoreum.data.oreum.repository

import com.jeong.jejuoreum.core.common.result.JejuOreumResult
import com.jeong.jejuoreum.core.common.result.JejuOreumResult.Companion.of
import com.jeong.jejuoreum.data.local.oreum.source.OreumLocalDataSource
import com.jeong.jejuoreum.data.oreum.mapper.toDomain
import com.jeong.jejuoreum.data.oreum.mapper.toEntity
import com.jeong.jejuoreum.data.remote.oreum.source.OreumRemoteDataSource
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OreumRepositoryImpl @Inject constructor(
    private val localDataSource: OreumLocalDataSource,
    private val remoteDataSource: OreumRemoteDataSource
) : OreumRepository {

    override fun observeOreums(): Flow<List<Oreum>> =
        localDataSource.observeOreums().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun refreshOreums(): JejuOreumResult<Unit> = of {
        val remote = remoteDataSource.fetchOreums().map { it.toEntity() }
        localDataSource.saveOreums(remote)
    }
}

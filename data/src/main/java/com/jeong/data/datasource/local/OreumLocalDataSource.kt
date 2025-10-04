package com.jeong.data.datasource.local

import com.jeong.data.model.OreumEntity
import kotlinx.coroutines.flow.Flow

interface OreumLocalDataSource {
    fun observeOreums(): Flow<List<OreumEntity>>
    suspend fun upsertAll(entities: List<OreumEntity>)
    suspend fun upsert(entity: OreumEntity)
    suspend fun findById(id: String): OreumEntity?
}

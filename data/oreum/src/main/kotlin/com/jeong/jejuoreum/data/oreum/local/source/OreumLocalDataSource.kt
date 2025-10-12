package com.jeong.jejuoreum.data.oreum.local.source

import com.jeong.jejuoreum.data.oreum.local.model.OreumEntity
import kotlinx.coroutines.flow.Flow

interface OreumLocalDataSource {
    fun observeOreums(): Flow<List<OreumEntity>>
    suspend fun upsertAll(entities: List<OreumEntity>)
    suspend fun upsert(entity: OreumEntity)
    suspend fun findById(id: String): OreumEntity?
}

package com.jeong.jejuoreum.data.local.oreum.source

import com.jeong.jejuoreum.data.local.oreum.model.OreumEntity
import kotlinx.coroutines.flow.Flow

interface OreumLocalDataSource {
    fun observeOreums(): Flow<List<OreumEntity>>
    suspend fun saveOreums(oreums: List<OreumEntity>)
}

package com.jeong.data.datasource.local

import com.jeong.data.model.OreumEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class InMemoryOreumLocalDataSource @Inject constructor() : OreumLocalDataSource {

    private val cachedOreums = MutableStateFlow<List<OreumEntity>>(emptyList())

    override fun observeOreums(): Flow<List<OreumEntity>> = cachedOreums.asStateFlow()

    override suspend fun upsertAll(entities: List<OreumEntity>) {
        cachedOreums.value = entities.associateBy { it.id }
            .let { current ->
                val merged = cachedOreums.value.associateBy { it.id } + current
                merged.values.sortedBy { it.name }
            }
    }

    override suspend fun upsert(entity: OreumEntity) {
        val current = cachedOreums.value.associateBy { it.id } + (entity.id to entity)
        cachedOreums.value = current.values.sortedBy { it.name }
    }

    override suspend fun findById(id: String): OreumEntity? =
        cachedOreums.value.firstOrNull { it.id == id }
}

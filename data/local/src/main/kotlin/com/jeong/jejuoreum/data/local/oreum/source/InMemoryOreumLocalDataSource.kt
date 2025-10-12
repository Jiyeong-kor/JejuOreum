package com.jeong.jejuoreum.data.local.oreum.source

import com.jeong.jejuoreum.data.local.oreum.model.OreumEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryOreumLocalDataSource @Inject constructor() : OreumLocalDataSource {
    private val cache = MutableStateFlow<List<OreumEntity>>(emptyList())

    override fun observeOreums(): Flow<List<OreumEntity>> = cache.asStateFlow()

    override suspend fun saveOreums(oreums: List<OreumEntity>) {
        cache.value = oreums
    }
}

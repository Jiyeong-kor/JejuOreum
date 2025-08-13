package com.jeong.jjoreum.repository

import com.jeong.jjoreum.data.model.api.ResultSummary
import kotlinx.coroutines.flow.StateFlow

interface OreumRepository {
    val oreumListFlow: StateFlow<List<ResultSummary>>
    suspend fun loadOreumListIfNeeded()
    fun getCachedOreumList(): List<ResultSummary>
    suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary
    suspend fun refreshAllOreumsWithNewUserData()
}
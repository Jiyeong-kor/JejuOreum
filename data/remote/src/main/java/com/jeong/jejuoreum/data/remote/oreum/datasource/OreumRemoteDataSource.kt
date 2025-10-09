package com.jeong.jejuoreum.data.remote.oreum.datasource

import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary

interface OreumRemoteDataSource {
    suspend fun fetchOreums(): List<ResultSummary>
    suspend fun fetchOreum(id: String): ResultSummary?
}

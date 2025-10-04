package com.jeong.data.datasource.remote

import com.jeong.domain.entity.ResultSummary

interface OreumRemoteDataSource {
    suspend fun fetchOreums(): List<ResultSummary>
    suspend fun fetchOreum(id: String): ResultSummary?
}

package com.jeong.jejuoreum.data.datasource.remote

import com.jeong.jejuoreum.domain.entity.ResultSummary

interface OreumRemoteDataSource {
    suspend fun fetchOreums(): List<ResultSummary>
    suspend fun fetchOreum(id: String): ResultSummary?
}

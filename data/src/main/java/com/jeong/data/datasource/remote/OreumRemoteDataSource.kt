package com.jeong.data.datasource.remote

import com.jeong.data.model.OreumResponse

interface OreumRemoteDataSource {
    suspend fun fetchOreums(): List<OreumResponse>
    suspend fun fetchOreum(id: String): OreumResponse?
}

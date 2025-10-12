package com.jeong.jejuoreum.data.remote.oreum.source

import com.jeong.jejuoreum.data.remote.oreum.dto.OreumDto

interface OreumRemoteDataSource {
    suspend fun fetchOreums(): List<OreumDto>
}

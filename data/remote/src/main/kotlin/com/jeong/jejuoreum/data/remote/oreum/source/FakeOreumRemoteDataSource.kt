package com.jeong.jejuoreum.data.remote.oreum.source

import com.jeong.jejuoreum.data.remote.oreum.dto.OreumDto
import javax.inject.Inject
import kotlinx.coroutines.delay

class FakeOreumRemoteDataSource @Inject constructor() : OreumRemoteDataSource {
    override suspend fun fetchOreums(): List<OreumDto> {
        delay(250)
        return listOf(
            OreumDto(id = 1L, name = "Seongsan Ilchulbong", elevation = 182, location = "Seongsan-eup"),
            OreumDto(id = 2L, name = "Geomun Oreum", elevation = 456, location = "Jocheon-eup"),
            OreumDto(id = 3L, name = "Darangshi Oreum", elevation = 382, location = "Seongsan-eup"),
            OreumDto(id = 4L, name = "Bulgmae Oreum", elevation = 210, location = "Aewol-eup")
        )
    }
}

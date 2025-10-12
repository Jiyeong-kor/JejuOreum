package com.jeong.jejuoreum.data.oreum.mapper

import com.jeong.jejuoreum.data.local.oreum.model.OreumEntity
import com.jeong.jejuoreum.data.remote.oreum.dto.OreumDto
import com.jeong.jejuoreum.domain.oreum.model.Oreum

fun OreumDto.toEntity(): OreumEntity = OreumEntity(
    id = id,
    name = name,
    elevation = elevation,
    location = location
)

fun OreumEntity.toDomain(): Oreum = Oreum(
    id = id,
    name = name,
    elevation = elevation,
    location = location
)

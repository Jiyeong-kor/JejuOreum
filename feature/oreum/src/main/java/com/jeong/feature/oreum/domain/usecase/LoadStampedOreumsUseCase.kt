package com.jeong.feature.oreum.domain.usecase

import com.jeong.domain.entity.MyStampItem
import com.jeong.domain.repository.OreumRepository
import javax.inject.Inject

class LoadStampedOreumsUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
) {

    suspend operator fun invoke(): Result<List<MyStampItem>> =
        oreumRepository.loadOreumListIfNeeded().mapCatching {
            oreumRepository.getCachedOreumList()
                .filter { it.userStamped }
                .map { summary ->
                    MyStampItem(
                        userId = 0,
                        oreumIdx = summary.idx,
                        oreumName = summary.oreumKname,
                        stampBoolean = true,
                    )
                }
        }
}

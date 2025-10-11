package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.entity.MyStampItem
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class LoadStampedOreumsUseCase @Inject constructor(
    private val oreumRepository: OreumRepository,
    private val refreshOreumsUseCase: RefreshOreumsUseCase,
) {

    suspend operator fun invoke(): Result<List<MyStampItem>> =
        refreshOreumsUseCase().mapCatching {
            oreumRepository.observeOreumSummaries()
                .first()
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

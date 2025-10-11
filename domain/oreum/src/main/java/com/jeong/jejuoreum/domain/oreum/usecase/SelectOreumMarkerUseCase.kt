package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject

class SelectOreumMarkerUseCase @Inject constructor(
    private val findOreumByLocationUseCase: FindOreumByLocationUseCase,
) {

    operator fun invoke(
        summaries: List<ResultSummary>,
        target: GeoPoint,
    ): ResultSummary? = findOreumByLocationUseCase(summaries, target)
}

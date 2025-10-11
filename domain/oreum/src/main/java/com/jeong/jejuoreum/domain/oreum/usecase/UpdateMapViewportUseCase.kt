package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.oreum.entity.GeoBounds
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject

class UpdateMapViewportUseCase @Inject constructor(
    private val filterOreumsWithinBoundsUseCase: FilterOreumsWithinBoundsUseCase,
) {

    operator fun invoke(
        oreums: List<ResultSummary>,
        bounds: GeoBounds,
    ): Result {
        val visibleSummaries = filterOreumsWithinBoundsUseCase(oreums, bounds)
        return Result(bounds = bounds, visibleSummaries = visibleSummaries)
    }

    data class Result(
        val bounds: GeoBounds,
        val visibleSummaries: List<ResultSummary>,
    )
}

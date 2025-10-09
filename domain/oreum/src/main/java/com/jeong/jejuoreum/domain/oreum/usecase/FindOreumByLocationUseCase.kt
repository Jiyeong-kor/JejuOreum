package com.jeong.jejuoreum.domain.usecase.oreum

import com.jeong.jejuoreum.domain.entity.GeoPoint
import com.jeong.jejuoreum.domain.entity.ResultSummary
import com.jeong.jejuoreum.domain.entity.quantized
import javax.inject.Inject

class FindOreumByLocationUseCase @Inject constructor() {
    operator fun invoke(oreums: List<ResultSummary>, target: GeoPoint): ResultSummary? {
        val quantizedTarget = target.quantized()
        return oreums.firstOrNull { summary ->
            GeoPoint(summary.y, summary.x).quantized() == quantizedTarget
        }
    }
}

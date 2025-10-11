package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import com.jeong.jejuoreum.domain.oreum.usecase.FindOreumByLocationUseCase
import com.jeong.jejuoreum.feature.map.presentation.model.OreumSummaryUiMapper
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class MapSelectionHandler @Inject constructor(
    private val findOreumByLocationUseCase: FindOreumByLocationUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val summaryUiMapper: OreumSummaryUiMapper,
) {

    suspend fun resolveSelection(
        point: GeoPoint,
        summaries: List<ResultSummary>,
    ): OreumSummaryUiModel? = withContext(dispatcherProvider.computation) {
        findOreumByLocationUseCase(summaries, point)?.let(summaryUiMapper::map)
    }
}

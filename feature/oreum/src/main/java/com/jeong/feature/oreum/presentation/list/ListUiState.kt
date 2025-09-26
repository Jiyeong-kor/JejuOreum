package com.jeong.feature.oreum.presentation.list

import com.jeong.domain.entity.ResultSummary

data class ListUiState(
    val oreums: List<ResultSummary> = emptyList(),
    val isLoading: Boolean = false
)

package com.jeong.jejuoreum.feature.map.presentation

import com.jeong.jejuoreum.core.common.UiState
import com.jeong.jejuoreum.domain.oreum.model.Oreum

data class MapUiState(
    val isLoading: Boolean = false,
    val oreums: List<Oreum> = emptyList(),
    val errorMessage: String? = null
) : UiState

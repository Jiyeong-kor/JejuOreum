package com.jeong.jejuoreum.feature.profile.presentation.stamp

import com.jeong.jejuoreum.core.architecture.UiText
import com.jeong.jejuoreum.core.common.UiState
import com.jeong.jejuoreum.domain.oreum.entity.MyStampItem

data class MyStampUiState(
    val isLoading: Boolean = true,
    val stampedList: List<MyStampItem> = emptyList(),
    val nickname: String = "",
    val errorMessage: UiText? = null,
) : UiState

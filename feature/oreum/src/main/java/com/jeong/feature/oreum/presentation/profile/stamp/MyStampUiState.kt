package com.jeong.feature.oreum.presentation.profile.stamp

import com.jeong.domain.entity.MyStampItem

data class MyStampUiState(
    val nickname: String = "",
    val stampedList: List<MyStampItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

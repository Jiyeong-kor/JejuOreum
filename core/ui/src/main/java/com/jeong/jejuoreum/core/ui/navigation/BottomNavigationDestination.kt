package com.jeong.jejuoreum.core.ui.navigation

import androidx.compose.runtime.Composable

interface BottomNavigationDestination {
    val route: String

    @Composable
    fun Icon(selected: Boolean)

    @Composable
    fun Label()
}

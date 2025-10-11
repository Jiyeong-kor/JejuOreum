package com.jeong.jejuoreum.core.navigation

import androidx.compose.runtime.Composable

interface BottomNavigationDestination : NavigationDestination {
    @Composable
    fun Icon(selected: Boolean)

    @Composable
    fun Label()
}

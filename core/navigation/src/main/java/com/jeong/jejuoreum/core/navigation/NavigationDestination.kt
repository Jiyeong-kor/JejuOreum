package com.jeong.jejuoreum.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface NavigationDestination {
    val route: String

    fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder)
}

interface BottomNavigationDestination : NavigationDestination {
    @Composable
    fun Icon(selected: Boolean)

    @Composable
    fun Label()
}

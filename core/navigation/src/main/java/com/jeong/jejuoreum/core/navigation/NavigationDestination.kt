package com.jeong.jejuoreum.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface NavigationDestination {
    val route: String

    fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder)
}

package com.jeong.jejuoreum.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface MapNavigation : BottomNavigationDestination {
    override val route: String

    fun navigateToMap(navController: NavController)

    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    )

    override fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
        registerGraph(navGraphBuilder = navGraphBuilder, navController = navController)
    }
}

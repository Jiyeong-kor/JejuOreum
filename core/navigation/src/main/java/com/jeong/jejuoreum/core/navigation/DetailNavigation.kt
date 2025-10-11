package com.jeong.jejuoreum.core.navigation

import android.os.Parcelable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface DetailNavigation : NavigationDestination {
    val route: String

    fun navigateToDetail(navController: NavController, oreum: Parcelable)

    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        onBack: () -> Unit
    )

    override fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
        registerGraph(
            navGraphBuilder = navGraphBuilder,
            navController = navController,
            onBack = { navController.popBackStack() }
        )
    }
}

package com.jeong.feature.oreum.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jeong.feature.oreum.presentation.ui.OreumRoute

const val OREUM_ROUTE = "oreum"

fun NavController.navigateToOreum(navOptions: NavOptions? = null) {
    navigate(OREUM_ROUTE, navOptions)
}

fun NavGraphBuilder.oreumScreen(
    onNavigateToDetail: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    composable(route = OREUM_ROUTE) {
        OreumRoute(
            onNavigateToDetail = onNavigateToDetail,
            onError = onError
        )
    }
}

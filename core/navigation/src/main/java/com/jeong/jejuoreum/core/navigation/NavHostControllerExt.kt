package com.jeong.jejuoreum.core.navigation

import androidx.navigation.NavHostController

fun NavHostController.navigateToRoot(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

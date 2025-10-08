package com.jeong.feature.join.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jeong.feature.join.presentation.JoinRoute

object JoinNavigation {
    const val ROUTE: String = "join"
}

fun NavGraphBuilder.joinScreen(onNavigateToMain: () -> Unit) {
    composable(route = JoinNavigation.ROUTE) {
        JoinRoute(onNavigateToMain = onNavigateToMain)
    }
}

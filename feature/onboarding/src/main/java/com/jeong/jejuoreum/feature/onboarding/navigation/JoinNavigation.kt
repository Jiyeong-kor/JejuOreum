package com.jeong.jejuoreum.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.feature.onboarding.presentation.JoinRoute

object JoinNavigation {
    const val ROUTE: String = "join"
}

fun NavGraphBuilder.joinScreen(onNavigateToMain: () -> Unit) {
    composable(route = JoinNavigation.ROUTE) {
        JoinRoute(onNavigateToMain = onNavigateToMain)
    }
}

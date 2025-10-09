package com.jeong.jejuoreum.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.navigation.NavigationDestination
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.navigation.navigateToRoot
import com.jeong.jejuoreum.feature.onboarding.presentation.JoinRoute

object JoinNavigation {
    const val ROUTE: String = "join"
}

fun NavGraphBuilder.joinScreen(onNavigateToMain: () -> Unit) {
    composable(route = JoinNavigation.ROUTE) {
        JoinRoute(onNavigateToMain = onNavigateToMain)
    }
}

object JoinRouteContract : NavigationDestination {
    override val route: String = JoinNavigation.ROUTE

    override fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.joinScreen {
            navController.navigateToRoot(OreumNavigation.MAP)
            navController.popBackStack(route, inclusive = true)
        }
    }
}

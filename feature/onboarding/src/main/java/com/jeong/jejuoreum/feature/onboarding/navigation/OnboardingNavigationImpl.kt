package com.jeong.jejuoreum.feature.onboarding.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.navigation.OnboardingNavigation
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.navigation.navigateToRoot
import com.jeong.jejuoreum.feature.onboarding.presentation.JoinRoute
import javax.inject.Inject

class OnboardingNavigationImpl @Inject constructor() : OnboardingNavigation {
    override val route: String = OreumNavigation.Onboarding.ROUTE

    override fun navigateToOnboarding(navController: NavController) {
        val hostController = navController as? NavHostController
        if (hostController != null) {
            hostController.navigate(route) {
                popUpTo(hostController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navController.navigate(route)
        }
    }

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    ) {
        navGraphBuilder.composable(route) {
            JoinRoute(
                onNavigateToMain = {
                    val hostController = navController as? NavHostController
                    if (hostController != null) {
                        hostController.navigateToRoot(OreumNavigation.Map.ROUTE)
                    } else {
                        navController.navigate(OreumNavigation.Map.ROUTE)
                    }
                    navController.popBackStack(route, inclusive = true)
                }
            )
        }
    }
}

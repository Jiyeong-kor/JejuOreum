package com.jeong.jejuoreum.feature.splash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.navigation.SplashNavigation
import com.jeong.jejuoreum.feature.splash.presentation.SplashRoute
import javax.inject.Inject

class SplashNavigationImpl @Inject constructor() : SplashNavigation {
    override val route: String = OreumNavigation.SPLASH

    override fun navigateToSplash(navController: NavController) {
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
            SplashRoute(
                onNavigateToOnboarding = {
                    navController.navigate(OreumNavigation.ONBOARDING) {
                        popUpTo(route) { inclusive = true }
                    }
                },
                onNavigateToMap = {
                    navController.navigate(OreumNavigation.MAP) {
                        popUpTo(route) { inclusive = true }
                    }
                }
            )
        }
    }
}

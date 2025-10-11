package com.jeong.jejuoreum.feature.profile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.navigation.ProfileNavigation
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDefaults
import com.jeong.jejuoreum.feature.profile.presentation.profile.MyRoute
import javax.inject.Inject

class ProfileNavigationImpl @Inject constructor() : ProfileNavigation {
    override val route: String = OreumNavigation.Profile.ROUTE

    @Composable
    override fun Icon(selected: Boolean) = BottomNavigationDefaults.Profile.Icon(selected)

    @Composable
    override fun Label() = BottomNavigationDefaults.Profile.Label()

    override fun navigateToProfile(navController: NavController) {
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
            MyRoute(
                onFavoriteItemClick = { oreum ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(OreumNavigation.Detail.INITIAL_OREUM_KEY, oreum)
                    navController.navigate(OreumNavigation.Detail.ROUTE)
                },
                onNavigateToWriteReview = { idx, name ->
                    navController.navigate(OreumNavigation.Review.route(idx, name))
                }
            )
        }
    }
}

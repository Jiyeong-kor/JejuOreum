package com.jeong.jejuoreum.feature.map.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.navigation.MapNavigation
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDefaults
import com.jeong.jejuoreum.feature.map.presentation.map.MapRoute
import javax.inject.Inject

class MapNavigationImpl @Inject constructor() : MapNavigation {
    override val route: String = OreumNavigation.MAP

    @Composable
    override fun Icon(selected: Boolean) = BottomNavigationDefaults.Map.Icon(selected)

    @Composable
    override fun Label() = BottomNavigationDefaults.Map.Label()

    override fun navigateToMap(navController: NavController) {
        val hostController = navController as? NavHostController
        if (hostController != null) {
            hostController.navigate(OreumNavigation.MAP) {
                popUpTo(hostController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navController.navigate(OreumNavigation.MAP)
        }
    }

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    ) {
        navGraphBuilder.composable(route) {
            MapRoute(
                onNavigateToDetail = { oreum ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(OreumNavigation.DETAIL_OREUM_KEY, oreum)
                    navController.navigate(OreumNavigation.DETAIL)
                }
            )
        }
    }
}

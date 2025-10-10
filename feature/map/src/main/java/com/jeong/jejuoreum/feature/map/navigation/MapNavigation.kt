package com.jeong.jejuoreum.feature.map.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.feature.map.presentation.map.MapRoute
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDestination
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDefaults

object MapNavigation : BottomNavigationDestination {
    override val route: String = OreumNavigation.MAP

    @Composable
    override fun Icon(selected: Boolean) = BottomNavigationDefaults.Map.Icon(selected)

    @Composable
    override fun Label() = BottomNavigationDefaults.Map.Label()

    override fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
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

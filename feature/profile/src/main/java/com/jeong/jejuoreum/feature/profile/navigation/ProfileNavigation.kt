package com.jeong.jejuoreum.feature.profile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.feature.profile.presentation.profile.MyRoute
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDestination
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDefaults

object ProfileNavigation : BottomNavigationDestination {
    override val route: String = OreumNavigation.MY

    @Composable
    override fun Icon(selected: Boolean) = BottomNavigationDefaults.Profile.Icon(selected)

    @Composable
    override fun Label() = BottomNavigationDefaults.Profile.Label()

    override fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(route) {
            MyRoute(
                onFavoriteItemClick = { oreum ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(OreumNavigation.DETAIL_OREUM_KEY, oreum)
                    navController.navigate(OreumNavigation.DETAIL)
                },
                onNavigateToWriteReview = { idx, name ->
                    navController.navigate(OreumNavigation.writeReviewRoute(idx, name))
                }
            )
        }
    }
}

package com.jeong.jejuoreum.feature.map.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.designsystem.R as DesignSystemR
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.feature.map.presentation.map.MapRoute
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDestination

object MapNavigation : BottomNavigationDestination {
    override val route: String = OreumNavigation.MAP

    @Composable
    override fun Icon(selected: Boolean) {
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_map),
            contentDescription = stringResource(DesignSystemR.string.map_title)
        )
    }

    @Composable
    override fun Label() {
        Text(text = stringResource(id = DesignSystemR.string.map_title))
    }

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

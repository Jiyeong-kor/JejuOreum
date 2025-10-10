package com.jeong.jejuoreum.feature.map.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.navigation.NavigationDestination
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDestination
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDefaults

object ListPlaceholderDestination : NavigationDestination, BottomNavigationDestination {
    override val route: String = OreumNavigation.LIST

    @Composable
    override fun Icon(selected: Boolean) = BottomNavigationDefaults.List.Icon(selected)

    @Composable
    override fun Label() = BottomNavigationDefaults.List.Label()

    override fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(route) {
            ListPlaceholderRoute()
        }
    }
}

@Composable
private fun ListPlaceholderRoute() {
    Text(text = "List feature migration pending")
}

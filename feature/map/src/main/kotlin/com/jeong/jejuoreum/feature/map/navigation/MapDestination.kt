package com.jeong.jejuoreum.feature.map.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.navigation.ComposableDestination
import com.jeong.jejuoreum.feature.map.presentation.MapRoute

object MapDestination : ComposableDestination {
    override val route: String = "map"

    override fun NavGraphBuilder.composableDestination() {
        composable(route) {
            MapRoute()
        }
    }
}

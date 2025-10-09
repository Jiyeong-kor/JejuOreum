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
import com.jeong.jejuoreum.core.navigation.NavigationDestination
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.ui.navigation.BottomNavigationDestination

object ListPlaceholderDestination : NavigationDestination, BottomNavigationDestination {
    override val route: String = OreumNavigation.LIST

    @Composable
    override fun Icon(selected: Boolean) {
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_list),
            contentDescription = stringResource(DesignSystemR.string.list_title)
        )
    }

    @Composable
    override fun Label() {
        Text(text = stringResource(id = DesignSystemR.string.list_title))
    }

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

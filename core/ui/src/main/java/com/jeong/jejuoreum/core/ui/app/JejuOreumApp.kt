package com.jeong.jejuoreum.core.ui.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jeong.jejuoreum.core.navigation.BottomNavigationDestination
import com.jeong.jejuoreum.core.navigation.NavigationDestination
import com.jeong.jejuoreum.core.navigation.NavigationProvider
import com.jeong.jejuoreum.core.navigation.navigateToRoot
import com.jeong.jejuoreum.core.ui.dialog.NetworkDialog
import com.jeong.jejuoreum.core.ui.theme.JJOreumTheme

@Composable
fun JejuOreumApp(
    startDestination: String,
    navigationProvider: NavigationProvider,
    showNetworkDialog: Boolean,
    onRetryClick: () -> Unit,
) {
    JJOreumTheme {
        val detailNavigation = remember { navigationProvider.detailNavigation() }
        val mapNavigation = remember { navigationProvider.mapNavigation() }
        val onboardingNavigation = remember { navigationProvider.onboardingNavigation() }
        val profileNavigation = remember { navigationProvider.profileNavigation() }
        val splashNavigation = remember { navigationProvider.splashNavigation() }

        val destinations = remember(detailNavigation, mapNavigation, onboardingNavigation, profileNavigation, splashNavigation) {
            listOf(
                splashNavigation,
                onboardingNavigation,
                mapNavigation,
                profileNavigation,
                detailNavigation,
            )
        }
        val bottomDestinations = remember(mapNavigation, profileNavigation) {
            listOf(mapNavigation, profileNavigation)
        }

        MainScaffold(
            startDestination = startDestination,
            destinations = destinations,
            bottomDestinations = bottomDestinations,
        )
        if (showNetworkDialog) {
            NetworkDialog(onRetryClick = onRetryClick)
        }
    }
}

@Composable
fun MainScaffold(
    startDestination: String,
    destinations: List<NavigationDestination>,
    bottomDestinations: List<BottomNavigationDestination>,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: startDestination
    val distinctBottomDestinations = remember(bottomDestinations) { bottomDestinations.distinctBy { it.route } }
    val showBottomBar = distinctBottomDestinations.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    distinctBottomDestinations.forEach { destination ->
                        val selected = destination.route == currentRoute
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigateToRoot(destination.route) },
                            icon = { destination.Icon(selected) },
                            label = { destination.Label() }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            destinations.distinctBy(NavigationDestination::route)
                .forEach { destination ->
                    destination.register(navController, this)
                }
        }
    }
}

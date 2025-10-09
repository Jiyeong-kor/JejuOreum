package com.jeong.jejuoreum.core.navigation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jeong.jejuoreum.core.navigation.BottomNavigationDestination
import com.jeong.jejuoreum.core.navigation.NavHost
import com.jeong.jejuoreum.core.navigation.NavigationDestination
import com.jeong.jejuoreum.core.navigation.navigateToRoot

@Composable
fun MainRoute(
    startDestination: String,
    destinations: List<NavigationDestination>,
    bottomDestinations: List<BottomNavigationDestination>
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: startDestination
    val bottomBarDestinations = remember(bottomDestinations) { bottomDestinations.distinctBy { it.route } }
    val showBottomBar = bottomBarDestinations.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomBarDestinations.forEach { destination ->
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

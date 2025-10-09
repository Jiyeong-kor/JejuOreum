package com.jeong.jejuoreum.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost as ComposeNavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun NavHost(
    startDestination: String,
    navController: NavHostController = rememberNavController(),
    builder: NavGraphBuilder.() -> Unit
) {
    ComposeNavHost(
        navController = navController,
        startDestination = startDestination,
        builder = builder
    )
}

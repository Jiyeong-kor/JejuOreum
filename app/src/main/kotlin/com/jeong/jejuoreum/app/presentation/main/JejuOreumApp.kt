package com.jeong.jejuoreum.app.presentation.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jeong.jejuoreum.core.designsystem.theme.JejuOreumTheme
import com.jeong.jejuoreum.core.navigation.ComposableDestination

@Composable
fun JejuOreumApp(
    startDestination: String,
    destinations: Set<ComposableDestination>
) {
    val navController = rememberNavController()

    JejuOreumTheme {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            destinations.forEach { destination ->
                with(destination) { composableDestination() }
            }
        }
    }
}

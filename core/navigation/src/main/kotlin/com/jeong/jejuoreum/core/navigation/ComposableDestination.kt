package com.jeong.jejuoreum.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder

interface ComposableDestination : JejuOreumDestination {
    fun NavGraphBuilder.composableDestination()
}

interface RootGraph {
    val startDestination: String
    fun NavGraphBuilder.buildGraph()
}

interface DestinationRegistry {
    val destinations: Set<ComposableDestination>
}

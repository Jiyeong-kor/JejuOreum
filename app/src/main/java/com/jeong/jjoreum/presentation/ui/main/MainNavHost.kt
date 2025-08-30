package com.jeong.jjoreum.presentation.ui.main

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.ui.detail.DetailScreen
import com.jeong.jjoreum.presentation.ui.detail.DetailViewModel
import com.jeong.jjoreum.presentation.ui.list.ListScreen
import com.jeong.jjoreum.presentation.ui.map.MapScreen
import com.jeong.jjoreum.presentation.ui.profile.MyScreen
import com.jeong.jjoreum.presentation.ui.profile.review.WriteReviewRoute
import com.jeong.jjoreum.presentation.viewmodel.WriteReviewViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import com.jeong.jjoreum.presentation.ui.Join.JoinRoute

@Composable
fun MainNavHost(startDestination: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("map", "list", "my")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "map",
                        onClick = {
                            navController.navigate("map") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_map),
                                contentDescription = stringResource(R.string.map_title)
                            )
                        },
                        label = { Text(stringResource(R.string.map_title)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "list",
                        onClick = {
                            navController.navigate("list") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_list),
                                contentDescription = stringResource(R.string.list_title)
                            )
                        },
                        label = { Text(stringResource(R.string.list_title)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "my",
                        onClick = {
                            navController.navigate("my") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            val iconRes =
                                if (currentRoute == "my") R.drawable.ic_my_selected else R.drawable.ic_my_unselected
                            Icon(
                                painterResource(iconRes),
                                contentDescription = stringResource(R.string.my_title)
                            )
                        },
                        label = { Text(stringResource(R.string.my_title)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("join") {
                JoinRoute(
                    onNavigateToMain = {
                        navController.navigate("map") {
                            popUpTo("join") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("map") {
                MapScreen(
                    onNavigateToWriteReview = { idx, name ->
                        navController.navigate("writeReview/$idx/$name")
                    },
                    onFavoriteToggled = {}
                )
            }
            composable("list") {
                val context = LocalContext.current
                ListScreen(
                    onItemClick = { oreum ->
                        val json = Uri.encode(Gson().toJson(oreum))
                        navController.navigate("detail/$json")
                    },
                    showToast = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            composable("my") {
                MyScreen(
                    onFavoriteItemClick = { oreum ->
                        val json = Uri.encode(Gson().toJson(oreum))
                        navController.navigate("detail/$json")
                    },
                    onNavigateToWriteReview = { idx, name ->
                        navController.navigate("writeReview/$idx/$name")
                    }
                )
            }
            composable(
                route = "detail/{oreum}",
                arguments = listOf(navArgument("oreum") { type = NavType.StringType })
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("oreum") ?: return@composable
                val oreum = Gson().fromJson(json, ResultSummary::class.java)
                val context = LocalContext.current
                val vm: DetailViewModel = hiltViewModel()
                LaunchedEffect(oreum) { vm.setOreumDetail(oreum) }
                DetailScreen(
                    viewModel = vm,
                    onNavigateToWriteReview = { idx, name ->
                        navController.navigate("writeReview/$idx/$name")
                    },
                    showToast = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    onFavoriteToggled = {}
                )
            }
            composable(
                route = "writeReview/{oreumIdx}/{oreumName}",
                arguments = listOf(
                    navArgument("oreumIdx") { type = NavType.IntType },
                    navArgument("oreumName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val idx = backStackEntry.arguments?.getInt("oreumIdx") ?: -1
                val name = backStackEntry.arguments?.getString("oreumName") ?: ""
                val vm: WriteReviewViewModel = hiltViewModel()
                LaunchedEffect(Unit) { vm.init(idx, name) }
                WriteReviewRoute(viewModel = vm)
            }
        }
    }
}
package com.jeong.feature.main.presentation

import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.jeong.feature.join.navigation.JoinNavigation
import com.jeong.feature.join.navigation.joinScreen
import com.jeong.feature.main.R
import com.jeong.feature.oreum.navigation.OreumNavigation
import com.jeong.feature.oreum.presentation.detail.DetailRoute
import com.jeong.feature.oreum.presentation.list.ListRoute
import com.jeong.feature.oreum.presentation.map.MapRoute
import com.jeong.feature.oreum.presentation.profile.MyRoute
import com.jeong.feature.oreum.presentation.review.WriteReviewRoute
import com.jeong.feature.oreum.presentation.review.WriteReviewViewModel
import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel

@Composable
fun MainRoute(startDestination: String) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val gson = remember { Gson() }
    val navItems = remember { BottomNavigationItem.defaults }
    val currentRoute = backStackEntry?.destination?.route ?: startDestination
    val showBottomBar = navItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(
                    items = navItems,
                    currentRoute = currentRoute,
                    onNavigate = navController::navigateToRoot,
                )
            }
        }
    ) { innerPadding ->
        val context = LocalContext.current
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            joinScreen(
                onNavigateToMain = {
                    navController.navigateToRoot(OreumNavigation.MAP)
                    navController.popBackStack(JoinNavigation.ROUTE, inclusive = true)
                }
            )
            composable(OreumNavigation.MAP) {
                MapRoute(
                    onNavigateToWriteReview = { idx, name ->
                        navController.navigate(OreumNavigation.writeReviewRoute(idx, name))
                    },
                    onFavoriteToggled = {},
                )
            }
            composable(OreumNavigation.LIST) {
                ListRoute(
                    onItemClick = { oreum ->
                        val json = Uri.encode(gson.toJson(oreum))
                        navController.navigate(OreumNavigation.detailRoute(json))
                    },
                    showToast = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            composable(OreumNavigation.MY) {
                MyRoute(
                    onFavoriteItemClick = { oreum ->
                        val json = Uri.encode(gson.toJson(oreum))
                        navController.navigate(OreumNavigation.detailRoute(json))
                    },
                    onNavigateToWriteReview = { idx, name ->
                        navController.navigate(OreumNavigation.writeReviewRoute(idx, name))
                    }
                )
            }
            composable(
                route = "${OreumNavigation.DETAIL}/{${OreumNavigation.DETAIL_ARG}}",
                arguments = listOf(
                    navArgument(OreumNavigation.DETAIL_ARG) { type = NavType.StringType }
                )
            ) { entry ->
                val json =
                    entry.arguments?.getString(OreumNavigation.DETAIL_ARG) ?: return@composable
                val oreum = gson.fromJson(json, OreumSummaryUiModel::class.java)
                DetailRoute(
                    initialOreum = oreum,
                    onNavigateToWriteReview = { idx, name ->
                        navController.navigate(OreumNavigation.writeReviewRoute(idx, name))
                    },
                    showToast = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    },
                    onFavoriteToggled = {},
                )
            }
            composable(
                route = "${OreumNavigation.WRITE_REVIEW}/" +
                        "{${OreumNavigation.WRITE_REVIEW_ARG_IDX}}/" +
                        "{${OreumNavigation.WRITE_REVIEW_ARG_NAME}}",
                arguments = listOf(
                    navArgument(OreumNavigation.WRITE_REVIEW_ARG_IDX) {
                        type = NavType.IntType
                    },
                    navArgument(OreumNavigation.WRITE_REVIEW_ARG_NAME) {
                        type = NavType.StringType
                    }
                )
            ) { entry ->
                val idx = entry.arguments?.getInt(OreumNavigation.WRITE_REVIEW_ARG_IDX)
                    ?: return@composable
                val name =
                    entry.arguments?.getString(OreumNavigation.WRITE_REVIEW_ARG_NAME).orEmpty()
                val viewModel: WriteReviewViewModel = hiltViewModel()
                LaunchedEffect(idx, name) { viewModel.init(idx, name) }
                WriteReviewRoute(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun MainBottomBar(
    items: List<BottomNavigationItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
) {
    NavigationBar {
        items.forEach { item ->
            val selected = item.route == currentRoute
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes(selected)),
                        contentDescription = stringResource(item.labelRes)
                    )
                },
                label = { Text(stringResource(item.labelRes)) },
            )
        }
    }
}

private data class BottomNavigationItem(
    val route: String,
    @field:DrawableRes private val defaultIconRes: Int,
    @field:DrawableRes private val selectedIconRes: Int,
    @field:StringRes val labelRes: Int
) {
    @DrawableRes
    fun iconRes(selected: Boolean): Int = if (selected) selectedIconRes else defaultIconRes

    companion object {
        val defaults: List<BottomNavigationItem>
            @Composable
            get() = listOf(
                BottomNavigationItem(
                    route = OreumNavigation.MAP,
                    defaultIconRes = R.drawable.ic_map,
                    selectedIconRes = R.drawable.ic_map,
                    labelRes = R.string.map_title,
                ),
                BottomNavigationItem(
                    route = OreumNavigation.LIST,
                    defaultIconRes = R.drawable.ic_list,
                    selectedIconRes = R.drawable.ic_list,
                    labelRes = R.string.list_title,
                ),
                BottomNavigationItem(
                    route = OreumNavigation.MY,
                    defaultIconRes = R.drawable.ic_my_unselected,
                    selectedIconRes = R.drawable.ic_my_selected,
                    labelRes = R.string.my_title,
                ),
            )
    }
}

private fun NavController.navigateToRoot(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

package com.jeong.jejuoreum.core.navigation.main

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
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jeong.jejuoreum.core.navigation.NavHost
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.navigation.R
import com.jeong.jejuoreum.feature.detail.presentation.detail.DetailRoute
import com.jeong.jejuoreum.feature.detail.presentation.review.WriteReviewRoute
import com.jeong.jejuoreum.feature.detail.presentation.review.WriteReviewViewModel
import com.jeong.jejuoreum.feature.map.presentation.map.MapRoute
import com.jeong.jejuoreum.feature.map.presentation.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.onboarding.navigation.JoinNavigation
import com.jeong.jejuoreum.feature.onboarding.navigation.joinScreen
import com.jeong.jejuoreum.feature.profile.presentation.profile.MyRoute

@Composable
fun MainRoute(startDestination: String) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
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
                ListPlaceholderRoute(
                    onShowMessage = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            composable(OreumNavigation.MY) {
                MyRoute(
                    onFavoriteItemClick = { oreum ->
                        navController.navigateToDetail(oreum)
                    },
                    onNavigateToWriteReview = { idx, name ->
                        navController.navigate(OreumNavigation.writeReviewRoute(idx, name))
                    }
                )
            }
            composable(OreumNavigation.DETAIL) { entry ->
                val initialOreum = remember(entry, navController.previousBackStackEntry) {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.remove<OreumSummaryUiModel>(OreumNavigation.DETAIL_OREUM_KEY)
                        ?: entry.savedStateHandle.get<OreumSummaryUiModel>(OreumNavigation.DETAIL_OREUM_KEY)
                }
                DetailRoute(
                    initialOreum = initialOreum,
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

@Composable
private fun ListPlaceholderRoute(onShowMessage: (String) -> Unit) {
    LaunchedEffect(Unit) {
        onShowMessage("List feature migration pending")
    }
    Text(text = "List feature migration pending")
}

private fun NavController.navigateToRoot(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavController.navigateToDetail(oreum: OreumSummaryUiModel) {
    currentBackStackEntry
        ?.savedStateHandle
        ?.set(OreumNavigation.DETAIL_OREUM_KEY, oreum)
    navigate(OreumNavigation.DETAIL)
}

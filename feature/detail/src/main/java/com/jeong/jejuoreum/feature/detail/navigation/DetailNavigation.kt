package com.jeong.jejuoreum.feature.detail.navigation

import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jeong.jejuoreum.core.navigation.NavigationDestination
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.feature.detail.presentation.detail.DetailRoute
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.detail.presentation.review.WriteReviewRoute
import com.jeong.jejuoreum.feature.detail.presentation.review.WriteReviewUiEvent
import com.jeong.jejuoreum.feature.detail.presentation.review.WriteReviewViewModel

object DetailRouteContract : NavigationDestination {
    override val route: String = OreumNavigation.DETAIL

    override fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(route) { entry ->
            val context = LocalContext.current
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
                onFavoriteToggled = {}
            )
        }
    }
}

object WriteReviewRouteContract : NavigationDestination {
    override val route: String = OreumNavigation.WRITE_REVIEW

    override fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(
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
            val name = entry.arguments?.getString(OreumNavigation.WRITE_REVIEW_ARG_NAME).orEmpty()
            val viewModel: WriteReviewViewModel = hiltViewModel()
            LaunchedEffect(idx, name) {
                viewModel.onEvent(WriteReviewUiEvent.Initialize(idx, name))
            }
            WriteReviewRoute(viewModel = viewModel)
        }
    }
}

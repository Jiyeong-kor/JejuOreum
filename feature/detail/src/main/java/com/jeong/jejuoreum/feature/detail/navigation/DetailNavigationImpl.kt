package com.jeong.jejuoreum.feature.detail.navigation

import android.os.Parcelable
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jeong.jejuoreum.core.navigation.DetailNavigation
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.ui.extensions.asString
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.detail.presentation.detail.DetailRoute
import com.jeong.jejuoreum.feature.detail.presentation.review.WriteReviewRoute
import com.jeong.jejuoreum.feature.detail.presentation.review.WriteReviewViewModel
import javax.inject.Inject

class DetailNavigationImpl @Inject constructor() : DetailNavigation {
    override val route: String = OreumNavigation.Detail.ROUTE

    override fun navigateToDetail(navController: NavController, oreum: Parcelable) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set(OreumNavigation.Detail.INITIAL_OREUM_KEY, oreum)
        navController.navigate(OreumNavigation.Detail.ROUTE)
    }

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        onBack: () -> Unit
    ) {
        navGraphBuilder.composable(route) { entry ->
            val context = LocalContext.current
            val initialOreum = remember(entry, navController.previousBackStackEntry) {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.remove<OreumSummaryUiModel>(OreumNavigation.Detail.INITIAL_OREUM_KEY)
                    ?: entry.savedStateHandle.get<OreumSummaryUiModel>(OreumNavigation.Detail.INITIAL_OREUM_KEY)
            }
            DetailRoute(
                onNavigateToWriteReview = { idx, name ->
                    navController.navigate(OreumNavigation.Review.route(idx, name))
                },
                showToast = { message ->
                    Toast.makeText(context, message.asString(context), Toast.LENGTH_SHORT).show()
                },
                onFavoriteToggled = {},
                initialOreum = initialOreum
            )
        }

        navGraphBuilder.composable(
            route = "${OreumNavigation.Review.ROUTE}/" +
                    "{${OreumNavigation.Review.ARG_OREUM_ID}}/" +
                    "{${OreumNavigation.Review.ARG_OREUM_NAME}}",
            arguments = listOf(
                navArgument(OreumNavigation.Review.ARG_OREUM_ID) {
                    type = NavType.IntType
                },
                navArgument(OreumNavigation.Review.ARG_OREUM_NAME) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val context = LocalContext.current
            val oreumIdx = entry.arguments?.getInt(OreumNavigation.Review.ARG_OREUM_ID) ?: return@composable
            val oreumName = entry.arguments?.getString(OreumNavigation.Review.ARG_OREUM_NAME).orEmpty()
            val viewModel: WriteReviewViewModel = hiltViewModel()

            WriteReviewRoute(
                viewModel = viewModel,
                oreumIdx = oreumIdx,
                oreumName = oreumName,
                onNavigateUp = {
                    onBack()
                },
                showToast = { message ->
                    Toast.makeText(context, message.asString(context), Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

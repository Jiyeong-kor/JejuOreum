package com.jeong.jejuoreum.feature.detail.navigation

import android.os.Parcelable
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jeong.jejuoreum.core.navigation.DetailNavigation
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.detail.presentation.detail.DetailRoute
import com.jeong.jejuoreum.feature.detail.presentation.review.WriteReviewRoute
import com.jeong.jejuoreum.feature.detail.presentation.review.WriteReviewViewModel
import javax.inject.Inject

class DetailNavigationImpl @Inject constructor() : DetailNavigation {
    override val route: String = OreumNavigation.DETAIL

    override fun navigateToDetail(navController: NavController, oreum: Parcelable) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set(OreumNavigation.DETAIL_OREUM_KEY, oreum)
        navController.navigate(OreumNavigation.DETAIL)
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
                    ?.remove<OreumSummaryUiModel>(OreumNavigation.DETAIL_OREUM_KEY)
                    ?: entry.savedStateHandle.get<OreumSummaryUiModel>(OreumNavigation.DETAIL_OREUM_KEY)
            }
            DetailRoute(
                onNavigateToWriteReview = { idx, name ->
                    navController.navigate(OreumNavigation.writeReviewRoute(idx, name))
                },
                showToast = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                },
                onFavoriteToggled = {},
                initialOreum = initialOreum
            )
        }

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
            val context = LocalContext.current
            val oreumIdx = entry.arguments?.getInt(OreumNavigation.WRITE_REVIEW_ARG_IDX) ?: return@composable
            val oreumName = entry.arguments?.getString(OreumNavigation.WRITE_REVIEW_ARG_NAME).orEmpty()
            val viewModel: WriteReviewViewModel = hiltViewModel()

            WriteReviewRoute(
                viewModel = viewModel,
                oreumIdx = oreumIdx,
                oreumName = oreumName,
                onNavigateUp = {
                    onBack()
                },
                showToast = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

package com.jeong.jejuoreum.feature.profile.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jeong.jejuoreum.core.designsystem.R as DesignSystemR
import com.jeong.jejuoreum.core.navigation.BottomNavigationDestination
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.feature.map.presentation.model.toDetailUiModel
import com.jeong.jejuoreum.feature.profile.presentation.profile.MyRoute

object ProfileNavigation : BottomNavigationDestination {
    override val route: String = OreumNavigation.MY

    @Composable
    override fun Icon(selected: Boolean) {
        val iconRes = if (selected) {
            DesignSystemR.drawable.ic_my_selected
        } else {
            DesignSystemR.drawable.ic_my_unselected
        }
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(DesignSystemR.string.my_title)
        )
    }

    @Composable
    override fun Label() {
        Text(text = stringResource(id = DesignSystemR.string.my_title))
    }

    override fun register(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(route) {
            MyRoute(
                onFavoriteItemClick = { oreum ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(OreumNavigation.DETAIL_OREUM_KEY, oreum.toDetailUiModel())
                    navController.navigate(OreumNavigation.DETAIL)
                },
                onNavigateToWriteReview = { idx, name ->
                    navController.navigate(OreumNavigation.writeReviewRoute(idx, name))
                }
            )
        }
    }
}

package com.jeong.jejuoreum.feature.profile.presentation.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.jeong.jejuoreum.feature.profile.R
import com.jeong.jejuoreum.feature.map.presentation.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.profile.presentation.favorite.MyFavoriteScreen
import com.jeong.jejuoreum.feature.profile.presentation.stamp.MyStampScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyRoute(
    onFavoriteItemClick: (OreumSummaryUiModel) -> Unit,
    onNavigateToWriteReview: (Int, String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val titles = listOf(
        stringResource(id = R.string.oreum_tab_favorites),
        stringResource(id = R.string.oreum_tab_stamps)
    )

    Column {
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = { Text(title) }
                )
            }
        }
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> MyFavoriteScreen(onItemClick = onFavoriteItemClick)
                1 -> MyStampScreen(onNavigateToWriteReview = onNavigateToWriteReview)
            }
        }
    }
}

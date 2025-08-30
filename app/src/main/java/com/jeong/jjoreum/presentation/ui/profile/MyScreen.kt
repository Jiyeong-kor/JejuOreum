package com.jeong.jjoreum.presentation.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.ui.profile.favorite.MyFavoriteScreen
import com.jeong.jjoreum.presentation.ui.profile.stamp.MyStampScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyScreen(
    onFavoriteItemClick: (ResultSummary) -> Unit,
    onNavigateToWriteReview: (Int, String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val titles = listOf(
        stringResource(id = R.string.tab_favorites),
        stringResource(id = R.string.tab_stamps)
    )

    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
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
package com.jeong.jejuoreum.feature.profile.presentation.stamp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jejuoreum.core.designsystem.theme.spacing
import com.jeong.jejuoreum.core.ui.resources.DesignSystemAssets
import com.jeong.jejuoreum.domain.oreum.entity.MyStampItem
import com.jeong.jejuoreum.feature.profile.R

@Composable
fun MyStampScreen(
    onNavigateToWriteReview: (Int, String) -> Unit,
    viewModel: MyStampViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadStampedList() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { viewModel.clearError() }
    }

    val count = uiState.stampedList.size

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.profile_stamp_header_top_padding)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.oreum_my_stamp_title, uiState.nickname),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(
                modifier = Modifier.width(
                    dimensionResource(id = R.dimen.profile_stamp_header_icon_spacing)
                )
            )
            Image(
                painter = painterResource(id = DesignSystemAssets.Drawable.stamp),
                contentDescription = stringResource(
                    id = DesignSystemAssets.Strings.stampIconDescription
                ),
                modifier = Modifier.size(
                    dimensionResource(id = R.dimen.profile_stamp_header_icon_size)
                ),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Spacer(
                modifier = Modifier.width(
                    dimensionResource(id = R.dimen.profile_stamp_header_counter_spacing)
                )
            )
            Text(
                text = stringResource(id = R.string.oreum_stamp_count, count),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            count == 0 -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(id = R.string.oreum_empty_list))
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = dimensionResource(id = R.dimen.profile_stamp_grid_padding)),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    items(uiState.stampedList) { item ->
                        StampItem(item) {
                            onNavigateToWriteReview(item.oreumIdx, item.oreumName)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StampItem(
    item: MyStampItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.profile_stamp_item_padding))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = DesignSystemAssets.Drawable.stampMarkerSelected),
            contentDescription = stringResource(
                id = DesignSystemAssets.Strings.stampIconDescription
            ),
            modifier = Modifier.size(
                dimensionResource(id = R.dimen.profile_stamp_item_icon_size)
            )
        )
        Text(
            text = item.oreumName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

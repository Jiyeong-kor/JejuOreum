package com.jeong.jjoreum.presentation.ui.profile.stamp

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.entity.MyStampItem
import com.jeong.jjoreum.presentation.viewmodel.MyStampViewModel

@Composable
fun MyStampScreen(
    viewModel: MyStampViewModel = hiltViewModel(),
    onNavigateToWriteReview: (Int, String) -> Unit,
) {
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()
    val stampedList by viewModel.stampedList.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadStampedList() }

    val count = stampedList.size

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.my_stamp_margin_top)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.my_stamp_title, nickname),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.my_stamp_image_margin_start)))
            Image(
                painter = painterResource(id = R.drawable.ic_stamp),
                contentDescription = stringResource(id = R.string.desc_stamp_icon),
                modifier = Modifier.size(dimensionResource(id = R.dimen.my_stamp_image_size)),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                    MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.my_stamp_num_margin_start)))
            Text(
                text = stringResource(id = R.string.stamp_count, count),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (count == 0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = R.string.empty_list))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = dimensionResource(id = R.dimen.my_stamp_margin_top))
            ) {
                items(stampedList) { item ->
                    StampItem(item) {
                        onNavigateToWriteReview(item.oreumIdx, item.oreumName)
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
            .padding(dimensionResource(id = R.dimen.stamp_rv_item_padding))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.oreum_selected),
            contentDescription = stringResource(id = R.string.desc_stamp_icon),
            modifier = Modifier.size(dimensionResource(id = R.dimen.stamp_rv_item_image_size))
        )
        Text(
            text = item.oreumName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

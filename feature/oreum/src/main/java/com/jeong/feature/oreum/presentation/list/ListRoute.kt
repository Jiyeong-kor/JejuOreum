package com.jeong.feature.oreum.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.jeong.domain.entity.ResultSummary
import com.jeong.feature.oreum.R

@Composable
fun ListRoute(
    onItemClick: (ResultSummary) -> Unit,
    showToast: (String) -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
    val oreumList by viewModel.oreumList.collectAsState()
    val context = LocalContext.current
    val stampResult by remember { viewModel.stampResult }.collectAsState()
    val loadError by viewModel.loadError.collectAsState()

    LaunchedEffect(stampResult) {
        stampResult?.let { result ->
            if (result.isSuccess) {
                showToast(context.getString(R.string.oreum_stamp_success_message))
            } else {
                showToast(
                    result.exceptionOrNull()?.message
                        ?: context.getString(R.string.oreum_unknown_error_message)
                )
            }
            viewModel.clearStampResult()
        }
    }

    LaunchedEffect(loadError) {
        loadError?.let {
            showToast(it)
            viewModel.clearLoadError()
        }
    }

    ListScreen(
        oreumList = oreumList,
        onItemClick = onItemClick,
        onFavoriteClick = { summary ->
            viewModel.toggleFavorite(summary.idx.toString())
        },
        onStampClick = { summary ->
            viewModel.tryStamp(
                oreumIdx = summary.idx.toString(),
                oreumName = summary.oreumKname,
                oreumLat = summary.y,
                oreumLng = summary.x
            )
        }
    )
}

@Composable
private fun ListScreen(
    oreumList: List<ResultSummary>,
    onItemClick: (ResultSummary) -> Unit,
    onFavoriteClick: (ResultSummary) -> Unit,
    onStampClick: (ResultSummary) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = oreumList,
            key = { it.idx }
        ) { oreum ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                OreumListItem(
                    oreum = oreum,
                    onItemClick = onItemClick,
                    onFavoriteClick = onFavoriteClick,
                    onStampClick = onStampClick
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

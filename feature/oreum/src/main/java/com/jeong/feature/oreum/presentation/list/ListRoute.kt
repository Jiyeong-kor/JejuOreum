package com.jeong.feature.oreum.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.jeong.feature.oreum.R
import com.jeong.feature.oreum.presentation.model.OreumSummaryUiModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ListRoute(
    onItemClick: (OreumSummaryUiModel) -> Unit,
    showToast: (String) -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ListUiEffect.StampSuccess -> {
                    showToast(context.getString(R.string.oreum_stamp_success_message))
                }

                is ListUiEffect.StampFailure -> {
                    val message =
                        effect.reason ?: context.getString(R.string.oreum_unknown_error_message)
                    showToast(message)
                }

                is ListUiEffect.LoadFailed -> {
                    val message =
                        effect.reason ?: context.getString(R.string.oreum_unknown_error_message)
                    showToast(message)
                }

                is ListUiEffect.FavoriteUpdateFailed -> {
                    val message =
                        effect.reason
                            ?: context.getString(R.string.oreum_unknown_error_message)
                    showToast(message)
                }
            }
        }
    }

    ListScreen(
        state = uiState,
        onItemClick = onItemClick,
        onFavoriteClick = { viewModel.onEvent(ListUiEvent.FavoriteToggled(it)) },
        onStampClick = { viewModel.onEvent(ListUiEvent.StampRequested(it)) }
    )
}

@Composable
private fun ListScreen(
    state: ListUiState,
    onItemClick: (OreumSummaryUiModel) -> Unit,
    onFavoriteClick: (OreumSummaryUiModel) -> Unit,
    onStampClick: (OreumSummaryUiModel) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isEmpty) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(
                    items = state.oreums,
                    key = { it.idx },
                ) { oreum ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        OreumListItem(
                            oreum = oreum,
                            onItemClick = onItemClick,
                            onFavoriteClick = onFavoriteClick,
                            onStampClick = onStampClick,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.oreum_empty_list),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OreumListItem(
    oreum: OreumSummaryUiModel,
    onItemClick: (OreumSummaryUiModel) -> Unit,
    onFavoriteClick: (OreumSummaryUiModel) -> Unit,
    onStampClick: (OreumSummaryUiModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(oreum) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = oreum.imgPath,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            ),
                        ),
                        shape = MaterialTheme.shapes.medium,
                    ),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(16.dp))
            ColumnWithDescription(oreum)
        }

        Row {
            Icon(
                painter = painterResource(id = R.drawable.ic_stamp),
                contentDescription = stringResource(R.string.oreum_desc_stamp_icon),
                tint = if (oreum.userStamped) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .clickable { onStampClick(oreum) }
                    .padding(8.dp),
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = stringResource(R.string.oreum_desc_favorite_icon),
                tint = if (oreum.userLiked) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .clickable { onFavoriteClick(oreum) }
                    .padding(8.dp),
            )
        }
    }
}

@Composable
private fun ColumnWithDescription(oreum: OreumSummaryUiModel) {
    Column {
        Text(
            text = oreum.oreumKname,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = oreum.oreumAddr,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.oreum_stamp_count, oreum.totalStamps),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

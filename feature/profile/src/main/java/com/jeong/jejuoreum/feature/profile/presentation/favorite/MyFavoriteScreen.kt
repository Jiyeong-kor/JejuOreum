package com.jeong.jejuoreum.feature.profile.presentation.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jejuoreum.core.common.UiText
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.profile.R
import com.jeong.jejuoreum.feature.profile.presentation.list.OreumListItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyFavoriteScreen(
    onItemClick: (OreumSummaryUiModel) -> Unit,
    onShowMessage: (UiText) -> Unit = {},
    viewModel: MyFavoriteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MyFavoriteUiEffect.ShowError -> {
                    onShowMessage(effect.message)
                    viewModel.onEvent(MyFavoriteUiEvent.ErrorConsumed)
                }
            }
        }
    }

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.favorites.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = R.string.oreum_empty_list))
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.favorites, key = { it.idx }) { oreum ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        OreumListItem(
                            oreum = oreum,
                            onItemClick = onItemClick,
                            onFavoriteClick = {
                                viewModel.onEvent(
                                    MyFavoriteUiEvent.FavoriteToggled(
                                        oreumIdx = it.idx.toString(),
                                        currentlyLiked = it.userLiked,
                                    )
                                )
                            },
                            onStampClick = {},
                        )
                    }
                }
            }
        }
    }
}

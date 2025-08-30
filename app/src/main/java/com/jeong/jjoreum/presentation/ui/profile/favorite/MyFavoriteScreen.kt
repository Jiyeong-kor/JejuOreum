package com.jeong.jjoreum.presentation.ui.profile.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.ui.list.ListItem
import com.jeong.jjoreum.presentation.viewmodel.MyFavoriteViewModel

@Composable
fun MyFavoriteScreen(
    viewModel: MyFavoriteViewModel = hiltViewModel(),
    onItemClick: (ResultSummary) -> Unit
) {
    val favoriteList by viewModel.favoriteList.collectAsStateWithLifecycle()

    if (favoriteList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = stringResource(id = R.string.empty_list))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favoriteList, key = { it.idx }) { oreum ->
                ListItem(
                    oreum = oreum,
                    onItemClick = onItemClick,
                    onFavoriteClick = {
                        viewModel.toggleFavorite(it.idx.toString(), !it.userLiked)
                    },
                    onStampClick = {}
                )
            }
        }
    }
}
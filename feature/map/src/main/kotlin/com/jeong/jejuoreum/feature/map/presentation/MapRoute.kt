package com.jeong.jejuoreum.feature.map.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeong.jejuoreum.feature.map.R

@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MapScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh
    )
}

@Composable
fun MapScreen(
    uiState: MapUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.map_title)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(id = R.string.map_refresh)
                )
            }
        }
    ) { padding ->
        MapContent(
            paddingValues = padding,
            uiState = uiState,
            onRefresh = onRefresh
        )
    }
}

@Composable
private fun MapContent(
    paddingValues: PaddingValues,
    uiState: MapUiState,
    onRefresh: () -> Unit
) {
    when {
        uiState.isLoading -> LoadingContent(paddingValues)
        uiState.oreums.isEmpty() -> EmptyContent(paddingValues, onRefresh)
        else -> OreumList(paddingValues, uiState)
    }
}

@Composable
private fun LoadingContent(padding: PaddingValues) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(
    padding: PaddingValues,
    onRefresh: () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(id = R.string.map_empty), style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(id = R.string.map_refresh)
                )
            }
        }
    }
}

@Composable
private fun OreumList(
    padding: PaddingValues,
    uiState: MapUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(uiState.oreums) { oreum ->
            androidx.compose.material3.Card {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = oreum.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = oreum.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(id = R.string.map_elevation_format, oreum.elevation),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

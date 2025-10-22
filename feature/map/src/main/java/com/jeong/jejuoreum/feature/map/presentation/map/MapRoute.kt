package com.jeong.jejuoreum.feature.map.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jejuoreum.core.ui.extensions.asString
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.map.presentation.map.MapEffect.ShowToast
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MapRoute(
    onNavigateToDetail: (OreumSummaryUiModel) -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val mapState by viewModel.uiState.collectAsStateWithLifecycle()

    MapScreen(
        uiState = mapState,
        onEvent = viewModel::onEvent,
        onNavigateToDetail = onNavigateToDetail,
    )

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ShowToast -> android.widget.Toast.makeText(
                    context,
                    effect.message.asString(context),
                    android.widget.Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}

object MapRouteDefaults {
    val noopNavigateToDetail: (OreumSummaryUiModel) -> Unit = { _ -> }
}

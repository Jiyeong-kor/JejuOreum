package com.jeong.jejuoreum.feature.map.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.map.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSheet(
    overlay: OreumSummaryUiModel?,
    controller: MapController?,
    onDismiss: () -> Unit,
    onNavigateToDetail: (OreumSummaryUiModel) -> Unit
) {
    if (overlay != null) {
        val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        DisposableEffect(Unit) {
            controller?.pause()
            onDispose { controller?.resume() }
        }
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(24.dp)
                ) {
                    Text(
                        text = overlay.oreumKname.ifBlank { overlay.oreumEname },
                        style = MaterialTheme.typography.titleLarge,
                    )
                    if (overlay.oreumAddr.isNotBlank()) {
                        Text(
                            text = overlay.oreumAddr,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = overlay.explain,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { onNavigateToDetail(overlay) }) {
                        Text(text = stringResource(id = R.string.map_view_detail))
                    }
                }
            }
        }
    }
}

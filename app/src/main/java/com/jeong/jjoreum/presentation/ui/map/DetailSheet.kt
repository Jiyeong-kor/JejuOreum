package com.jeong.jjoreum.presentation.ui.map

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.ui.detail.DetailScreen
import com.jeong.jjoreum.presentation.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSheet(
    overlay: ResultSummary?,
    detailVm: DetailViewModel,
    controller: MapController?,
    onDismiss: () -> Unit,
    onNavigateToWriteReview: (Int, String) -> Unit,
    onFavoriteToggled: (String) -> Unit
) {
    val context = LocalContext.current
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
                DetailScreen(
                    viewModel = detailVm,
                    onNavigateToWriteReview = onNavigateToWriteReview,
                    showToast = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    onFavoriteToggled = onFavoriteToggled
                )
            }
        }
    }
}

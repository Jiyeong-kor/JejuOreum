package com.jeong.jejuoreum.feature.detail.presentation.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jejuoreum.feature.detail.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WriteReviewRoute(
    viewModel: WriteReviewViewModel,
    oreumIdx: Int,
    oreumName: String,
    onNavigateUp: () -> Unit,
    showToast: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(oreumIdx, oreumName) {
        viewModel.onEvent(WriteReviewUiEvent.Initialize(oreumIdx, oreumName))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is WriteReviewUiEffect.ShowMessage -> showToast(effect.message)
            }
        }
    }

    WriteReviewScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateUp = onNavigateUp,
    )
}

@Composable
fun WriteReviewScreen(
    state: WriteReviewUiState,
    onEvent: (WriteReviewUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.write_review_title, state.oreumName),
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = state.reviewInput,
            onValueChange = { onEvent(WriteReviewUiEvent.ReviewTextChanged(it)) },
            label = { Text(stringResource(id = R.string.write_review_input_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onEvent(WriteReviewUiEvent.SaveClicked(state.oreumName)) },
            enabled = state.reviewInput.isNotBlank() && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.write_review_save_button))
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.reviews) { review ->
                    ReviewItem(
                        review = review,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: ReviewUiModel,
    onEvent: (WriteReviewUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = review.nickname, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = review.content)
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = review.createdAtFormatted, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.padding(4.dp))
        Button(onClick = { onEvent(WriteReviewUiEvent.LikeClicked(review)) }) {
            Text(text = stringResource(id = R.string.write_review_like_button))
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Button(onClick = { onEvent(WriteReviewUiEvent.DeleteClicked(review)) }) {
            Text(text = stringResource(id = R.string.write_review_delete_button))
        }
    }
}

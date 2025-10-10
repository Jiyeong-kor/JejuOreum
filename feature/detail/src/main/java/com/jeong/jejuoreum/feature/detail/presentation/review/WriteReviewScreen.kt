package com.jeong.jejuoreum.feature.detail.presentation.review

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jejuoreum.feature.detail.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WriteReviewRoute(
    viewModel: WriteReviewViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is WriteReviewUiEffect.ShowMessage ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    WriteReviewScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun WriteReviewScreen(
    state: WriteReviewUiState,
    onEvent: (WriteReviewUiEvent) -> Unit,
) {
    val anonymousLabel = stringResource(id = R.string.oreum_anonymous)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.oreumName,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.reviewInput,
            onValueChange = { onEvent(WriteReviewUiEvent.ReviewTextChanged(it)) },
            label = { Text(stringResource(id = R.string.oreum_review_input_hint)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onEvent(WriteReviewUiEvent.SaveClicked(anonymousLabel)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.reviewInput.isNotBlank() && !state.isSubmitting
        ) {
            Text(stringResource(id = R.string.oreum_save_review))
        }

        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        if (state.reviews.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(id = R.string.oreum_no_reviews),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.reviews) { review ->
                    ReviewItemCard(
                        review = review,
                        currentUserId = state.currentUserId,
                        onLikeClick = { onEvent(WriteReviewUiEvent.LikeClicked(it)) },
                        onDeleteClick = { onEvent(WriteReviewUiEvent.DeleteClicked(it)) },
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewItemCard(
    review: ReviewUiModel,
    currentUserId: String?,
    onLikeClick: (ReviewUiModel) -> Unit,
    onDeleteClick: (ReviewUiModel) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.oreum_profile_placeholder),
                    contentDescription = stringResource(id = R.string.oreum_review_profile_desc),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = review.userNickname, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = review.formattedTime(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (review.userId == currentUserId) {
                    IconButton(onClick = { onDeleteClick(review) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.oreum_review_delete_desc),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = review.userReview, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val likeIconColor =
                    if (review.isLiked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                IconButton(onClick = { onLikeClick(review) }) {
                    Icon(
                        imageVector = if (review.isLiked) Icons.Filled.Favorite
                        else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(id = R.string.oreum_review_like_desc),
                        tint = likeIconColor
                    )
                }
                Text(text = review.reviewLikeNum.toString())
            }
        }
    }
}

private fun ReviewUiModel.formattedTime(): String =
    java.text.SimpleDateFormat("yyyy.MM.dd HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(userTime))

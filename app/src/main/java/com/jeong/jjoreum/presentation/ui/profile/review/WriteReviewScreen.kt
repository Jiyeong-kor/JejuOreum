package com.jeong.jjoreum.presentation.ui.profile.review

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.entity.ReviewItem
import com.jeong.jjoreum.presentation.viewmodel.WriteReviewViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WriteReviewRoute(
    viewModel: WriteReviewViewModel
) {
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val oreumName by viewModel.oreumName.collectAsStateWithLifecycle()
    val reviewInputText by viewModel.reviewInputText.collectAsStateWithLifecycle()

    WriteReviewScreen(
        oreumName = oreumName,
        reviews = reviews,
        reviewInputText = reviewInputText,
        onReviewTextChange = viewModel::onReviewTextChange,
        onSaveClick = viewModel::saveReview,
        onLikeClick = viewModel::toggleReviewLike,
        onDeleteClick = viewModel::deleteReview
    )
}

@Composable
fun WriteReviewScreen(
    oreumName: String,
    reviews: List<ReviewItem>,
    reviewInputText: String,
    onReviewTextChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onLikeClick: (ReviewItem) -> Unit,
    onDeleteClick: (ReviewItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = oreumName,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = reviewInputText,

            // ViewModel에 텍스트 변경 알림
            onValueChange = onReviewTextChange,
            label = { Text("후기를 입력하세요") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(

            // ViewModel에 저장 이벤트 알림
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),

            // 텍스트가 있을 때만 활성화
            enabled = reviewInputText.isNotBlank()
        ) {
            Text("후기 저장")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        if (reviews.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "작성된 후기가 없습니다.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reviews) { review ->
                    ReviewItemCard(
                        review = review,
                        onLikeClick = onLikeClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewItemCard(
    review: ReviewItem,
    onLikeClick: (ReviewItem) -> Unit,
    onDeleteClick: (ReviewItem) -> Unit
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

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
                    painter = painterResource(id = R.drawable.ic_my_selected),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = review.userNickname, style = MaterialTheme.typography.titleMedium)
                    val formattedTime = SimpleDateFormat(
                        "yyyy.MM.dd HH:mm",
                        Locale.getDefault()
                    ).format(Date(review.userTime))
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (review.userId == currentUserId) {
                    IconButton(onClick = { onDeleteClick(review) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
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
                        contentDescription = "Like",
                        tint = likeIconColor
                    )
                }
                Text(text = review.reviewLikeNum.toString())
            }
        }
    }
}
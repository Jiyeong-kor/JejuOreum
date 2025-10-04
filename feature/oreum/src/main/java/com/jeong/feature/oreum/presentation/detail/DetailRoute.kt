package com.jeong.feature.oreum.presentation.detail

import android.Manifest
import android.R.drawable.ic_menu_delete
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jeong.domain.entity.ResultSummary
import com.jeong.domain.entity.ReviewItem
import com.jeong.feature.oreum.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailRoute(
    viewModel: DetailViewModel = hiltViewModel(),
    onNavigateToWriteReview: (Int, String) -> Unit,
    showToast: (String) -> Unit,
    onFavoriteToggled: (String) -> Unit,
    initialOreum: ResultSummary? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val oreumDetail = uiState.oreumDetail
    val isFavorite = uiState.isFavorite
    val hasStamp = uiState.hasStamp
    val locationPermissionGranted = uiState.isLocationPermissionGranted
    val context = LocalContext.current

    LaunchedEffect(initialOreum) {
        initialOreum?.let(viewModel::initialize)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onLocationPermissionResult(isGranted)
        if (isGranted) {
            viewModel.stampOreum()
        } else {
            showToast(context.getString(R.string.oreum_permission_required_message))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is DetailViewModel.DetailEvent.StampSuccess -> {
                    showToast(context.getString(R.string.oreum_stamp_success_message))
                    onFavoriteToggled(viewModel.uiState.value.oreumDetail?.idx.toString())
                }

                is DetailViewModel.DetailEvent.StampFailure -> {
                    val message =
                        event.message ?: context.getString(R.string.oreum_unknown_error_message)
                    showToast(message)
                }
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            showToast(message)
            viewModel.clearError()
        }
    }
    DetailScreen(
        uiState = uiState,
        onFavoriteToggled = {
            val oreumIdx = oreumDetail?.idx?.toString() ?: return@DetailScreen
            val wasFavorite = isFavorite
            viewModel.toggleFavorite()
            showToast(
                context.getString(
                    if (!wasFavorite) R.string.oreum_favorite_added_message
                    else R.string.oreum_favorite_removed_message
                )
            )
            onFavoriteToggled(oreumIdx)
        },
        onStampClick = {
            if (hasStamp) {
                onNavigateToWriteReview(
                    oreumDetail?.idx ?: -1,
                    oreumDetail?.oreumKname ?: ""
                )
            } else {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        viewModel.stampOreum()
                    }

                    else -> {
                        when (locationPermissionGranted) {
                            false -> showToast(
                                context.getString(
                                    R.string.oreum_permission_required_message
                                )
                            )

                            else -> permissionLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        }
                    }
                }
            }
        },
        onNavigateToWriteReview = onNavigateToWriteReview
    )
}

@Composable
private fun DetailScreen(
    uiState: DetailUiState,
    onFavoriteToggled: () -> Unit,
    onStampClick: () -> Unit,
    onNavigateToWriteReview: (Int, String) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomButtonSection(
                isFavorite = uiState.isFavorite,
                hasStamp = uiState.hasStamp,
                onFavoriteClick = onFavoriteToggled,
                onStampClick = onStampClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            uiState.oreumDetail?.let { oreum ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    AsyncImage(
                        model = oreum.imgPath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (oreum.userStamped) {
                        Image(
                            painter = painterResource(id = R.drawable.oreum_stamp_badge),
                            contentDescription = stringResource(id = R.string.oreum_desc_stamp_icon),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .size(80.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = oreum.oreumKname,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = oreum.oreumAddr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = oreum.explain,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            ReviewSection(
                reviews = uiState.reviewList,
                oreumId = uiState.oreumDetail?.idx?.toString() ?: ""
            )
        }
    }
}

@Composable
private fun BottomButtonSection(
    isFavorite: Boolean,
    hasStamp: Boolean,
    onFavoriteClick: () -> Unit,
    onStampClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onFavoriteClick) {
            Icon(
                painter = painterResource(
                    id = if (isFavorite) R.drawable.oreum_favorite_selected
                    else R.drawable.oreum_favorite_unselected
                ),
                contentDescription = stringResource(id = R.string.oreum_desc_favorite_icon),
                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(36.dp)
            )
        }
        Button(
            onClick = onStampClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = if (hasStamp) {
                    stringResource(id = R.string.oreum_write_review)
                } else {
                    stringResource(id = R.string.oreum_detail_stamp)
                }
            )
        }
    }
}

@Composable
private fun ReviewSection(
    reviews: List<ReviewItem>,
    oreumId: String,
    onLikeClick: (String, String) -> Unit = { _, _ -> },
    onDeleteClick: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.oreum_review_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (reviews.isEmpty()) {
            Text(
                text = stringResource(id = R.string.oreum_no_reviews),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                reviews.forEach { review ->
                    ReviewItem(
                        review = review,
                        oreumId = oreumId,
                        onLikeClick = onLikeClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: ReviewItem,
    oreumId: String,
    onLikeClick: (String, String) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = review.userNickname,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val formattedDate = SimpleDateFormat(
                    "yyyy.MM.dd", Locale.getDefault()
                ).format(Date(review.userTime))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onDeleteClick(review.userReview) }) {
                Icon(
                    painter = painterResource(id = ic_menu_delete),
                    contentDescription = stringResource(id = R.string.oreum_review_delete_desc)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = review.userReview,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier = Modifier
                    .clickable { onLikeClick(review.userId, oreumId) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        id = if (review.isLiked) R.drawable.oreum_favorite_selected
                        else R.drawable.oreum_favorite_unselected
                    ),
                    contentDescription = stringResource(id = R.string.oreum_review_like_desc),
                    tint = if (review.isLiked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${'$'}{review.reviewLikeNum}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

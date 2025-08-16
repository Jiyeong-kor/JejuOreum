package com.jeong.jjoreum.presentation.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary

@Composable
fun ListScreen(
    viewModel: ListViewModel = hiltViewModel(),
    onItemClick: (ResultSummary) -> Unit,
    showToast: (String) -> Unit
) {
    val oreumList by viewModel.oreumList.collectAsState()

    val stampResult by remember { viewModel.stampResult }.collectAsState()
    LaunchedEffect(stampResult) {
        stampResult?.let { result ->
            if (result.isSuccess) {
                showToast("스탬프 완료!")
            } else {
                showToast(result.exceptionOrNull()?.message ?: "알 수 없는 오류")
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = oreumList,
            key = { it.idx }
        ) { oreum ->
            ListItem(
                oreum = oreum,
                onItemClick = onItemClick,
                onFavoriteClick = {
                    viewModel.toggleFavorite(it.idx.toString())
                },
                onStampClick = {
                    viewModel.tryStamp(
                        oreumIdx = it.idx.toString(),
                        oreumName = it.oreumKname,
                        oreumLat = it.y,
                        oreumLng = it.x
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ListItem(
    oreum: ResultSummary,
    onItemClick: (ResultSummary) -> Unit,
    onFavoriteClick: (ResultSummary) -> Unit,
    onStampClick: (ResultSummary) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(oreum) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            AsyncImage(
                model = oreum.imgPath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                            startY = 0f,
                            endY = 500f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = oreum.oreumKname,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onFavoriteClick(oreum) }
                    ) {
                        Icon(
                            painter = if (oreum.userLiked) {
                                painterResource(id = R.drawable.ic_favorite_selected)
                            } else {
                                painterResource(id = R.drawable.ic_favorite_unselected)
                            },
                            contentDescription = "Favorite",
                            tint = if (oreum.userLiked) Color.Red else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "${oreum.totalFavorites}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onStampClick(oreum) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_stamp_variant),
                            contentDescription = "Stamp",
                            tint = if (oreum.userStamped) MaterialTheme.colorScheme.primary
                            else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "${oreum.totalStamps}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
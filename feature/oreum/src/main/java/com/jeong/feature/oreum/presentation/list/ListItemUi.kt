package com.jeong.feature.oreum.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jeong.domain.entity.ResultSummary
import com.jeong.feature.oreum.R

@Composable
internal fun OreumListItem(
    oreum: ResultSummary,
    modifier: Modifier = Modifier,
    onItemClick: (ResultSummary) -> Unit,
    onFavoriteClick: (ResultSummary) -> Unit,
    onStampClick: (ResultSummary) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onItemClick(oreum) }
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
                            painterResource(id = R.drawable.oreum_favorite_selected)
                        } else {
                            painterResource(id = R.drawable.oreum_favorite_unselected)
                        },
                        contentDescription = stringResource(id = R.string.oreum_desc_favorite_icon),
                        tint = if (oreum.userLiked) Color.Red else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${'$'}{oreum.totalFavorites}",
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
                        painter = painterResource(id = R.drawable.oreum_stamp_variant),
                        contentDescription = stringResource(id = R.string.oreum_desc_stamp_icon),
                        tint = if (oreum.userStamped) MaterialTheme.colorScheme.primary
                        else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${'$'}{oreum.totalStamps}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

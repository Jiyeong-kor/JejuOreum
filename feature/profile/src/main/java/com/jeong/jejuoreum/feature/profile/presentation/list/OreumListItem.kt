package com.jeong.jejuoreum.feature.profile.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jeong.jejuoreum.core.ui.model.OreumSummaryUiModel
import com.jeong.jejuoreum.feature.profile.R

@Composable
fun OreumListItem(
    oreum: OreumSummaryUiModel,
    onItemClick: (OreumSummaryUiModel) -> Unit,
    onFavoriteClick: (OreumSummaryUiModel) -> Unit,
    onStampClick: (OreumSummaryUiModel) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(oreum) }
            .padding(16.dp)
    ) {
        Text(
            text = oreum.oreumKname.ifBlank { oreum.oreumEname },
            style = MaterialTheme.typography.titleMedium,
        )
        if (oreum.oreumAddr.isNotBlank()) {
            Text(
                text = oreum.oreumAddr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = "★ ${oreum.totalFavorites} · 리뷰 ${oreum.totalStamps}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = { onFavoriteClick(oreum) }) {
            val label = if (oreum.userLiked) {
                stringResource(id = R.string.profile_remove_favorite)
            } else {
                stringResource(id = R.string.profile_add_favorite)
            }
            Text(text = label)
        }
        TextButton(onClick = { onStampClick(oreum) }) {
            Text(text = stringResource(id = R.string.profile_write_review))
        }
    }
}
